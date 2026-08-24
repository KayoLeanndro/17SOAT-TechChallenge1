package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.ItemEstoque;
import com.kap.mechanics_api.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.domain.OrcamentoServico;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.ServicoItem;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.dto.movimentacaoestoque.MovimentacaoEstoqueResponseDTO;
import com.kap.mechanics_api.dto.movimentacaoestoque.RegistroEntradaMovimentacaoEstoqueRequestDTO;
import com.kap.mechanics_api.dto.movimentacaoestoque.RegistroSaidaMovimentacaoEstoqueRequestDTO;
import com.kap.mechanics_api.enums.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.exception.EstoqueInsuficienteException;
import com.kap.mechanics_api.exception.ItemEstoqueInativoException;
import com.kap.mechanics_api.exception.MovimentacaoEstoqueNaoEncontradaException;
import com.kap.mechanics_api.exception.OrdemServicoNaoEstaEmExecucaoException;
import com.kap.mechanics_api.exception.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.exception.UsuarioNaoEncontradoException;
import com.kap.mechanics_api.exception.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.exception.PeriodoMovimentacaoInvalidoException;
import com.kap.mechanics_api.repository.ItemEstoqueRepository;
import com.kap.mechanics_api.repository.MovimentacaoEstoqueRepository;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import com.kap.mechanics_api.repository.UsuarioRepository;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final ItemEstoqueRepository itemEstoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrcamentoServicoRepository orcamentoServicoRepository;
    private final ServicoItemRepository servicoItemRepository;

    public MovimentacaoEstoqueService(MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
                                      ItemEstoqueRepository itemEstoqueRepository,
                                      UsuarioRepository usuarioRepository,
                                      OrdemServicoRepository ordemServicoRepository,
                                      OrcamentoServicoRepository orcamentoServicoRepository,
                                      ServicoItemRepository servicoItemRepository) {
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.itemEstoqueRepository = itemEstoqueRepository;
        this.usuarioRepository = usuarioRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.orcamentoServicoRepository = orcamentoServicoRepository;
        this.servicoItemRepository = servicoItemRepository;
    }

    @Transactional
    public MovimentacaoEstoqueResponseDTO registrarEntrada(RegistroEntradaMovimentacaoEstoqueRequestDTO dto,
                                                            String usuarioLogin) {
        ItemEstoque item = buscarItemParaMovimentacao(dto.itemEstoqueId());
        Usuario usuario = buscarUsuario(usuarioLogin);
        MovimentacaoEstoque movimentacao = registrarEntrada(item, dto.quantidade(), usuario);

        return toResponse(movimentacao, item.getQuantidadeAtual());
    }

    @Transactional
    public MovimentacaoEstoqueResponseDTO registrarSaida(RegistroSaidaMovimentacaoEstoqueRequestDTO dto,
                                                          String usuarioLogin) {
        ItemEstoque item = buscarItemParaMovimentacao(dto.itemEstoqueId());
        Usuario usuario = buscarUsuario(usuarioLogin);
        OrdemServico ordemServico = buscarOrdemServico(dto.ordemServicoId());
        validarOrdemServicoEmExecucao(ordemServico);
        MovimentacaoEstoque movimentacao = registrarSaida(item, dto.quantidade(), usuario, ordemServico);

        return toResponse(movimentacao, item.getQuantidadeAtual());
    }

    @Transactional
    public void baixarItensDaOrdemServico(OrdemServico ordemServico) {
        validarOrdemServicoEmExecucao(ordemServico);

        Map<Integer, Integer> quantidadesPorItem = new HashMap<>();
        List<OrcamentoServico> servicosOrcados = orcamentoServicoRepository
                .findByOrcamento_Id(ordemServico.getOrcamento().getId());

        for (OrcamentoServico servicoOrcado : servicosOrcados) {
            List<ServicoItem> itensDoServico = servicoItemRepository
                    .findByServico_Id(servicoOrcado.getServico().getId());

            for (ServicoItem itemDoServico : itensDoServico) {
                quantidadesPorItem.merge(
                        itemDoServico.getItemEstoque().getId(),
                        itemDoServico.getQuantidadePadrao(),
                        Math::addExact
                );
            }
        }

        for (var entry : quantidadesPorItem.entrySet()) {
            ItemEstoque item = buscarItemParaMovimentacao(entry.getKey());
            registrarSaida(item, entry.getValue(), ordemServico.getUsuarioAtendente(), ordemServico);
        }
    }

    private MovimentacaoEstoque registrarEntrada(ItemEstoque item, Integer quantidade, Usuario usuario) {
        validarItemAtivo(item);
        item.setQuantidadeAtual(Math.addExact(item.getQuantidadeAtual(), quantidade));
        itemEstoqueRepository.save(item);

        return movimentacaoEstoqueRepository.save(new MovimentacaoEstoque(
                item, TipoMovimentacaoEstoque.ENTRADA, quantidade, usuario, null));
    }

    private MovimentacaoEstoque registrarSaida(ItemEstoque item, Integer quantidade, Usuario usuario,
                                               OrdemServico ordemServico) {
        validarItemAtivo(item);
        if (item.getQuantidadeAtual() < quantidade) {
            throw new EstoqueInsuficienteException(item.getId(), item.getQuantidadeAtual(), quantidade);
        }

        item.setQuantidadeAtual(item.getQuantidadeAtual() - quantidade);
        itemEstoqueRepository.save(item);

        return movimentacaoEstoqueRepository.save(new MovimentacaoEstoque(
                item,
                TipoMovimentacaoEstoque.SAIDA,
                quantidade,
                usuario,
                ordemServico
        ));
    }

    public List<MovimentacaoEstoqueResponseDTO> listar() {
        return montarRespostas(movimentacaoEstoqueRepository.findAllByOrderByDataHoraDesc());
    }

    public List<MovimentacaoEstoqueResponseDTO> listarPorItem(Integer itemEstoqueId) {
        return montarRespostas(movimentacaoEstoqueRepository.findByItemEstoque_IdOrderByDataHoraDesc(itemEstoqueId));
    }

    public List<MovimentacaoEstoqueResponseDTO> listarPorOrdemServico(Integer ordemServicoId) {
        return montarRespostas(movimentacaoEstoqueRepository.findByOrdemServico_IdOrderByDataHoraDesc(ordemServicoId));
    }

    public List<MovimentacaoEstoqueResponseDTO> listarPorTipo(TipoMovimentacaoEstoque tipo) {
        return montarRespostas(movimentacaoEstoqueRepository.findByTipoOrderByDataHoraDesc(tipo));
    }

    public List<MovimentacaoEstoqueResponseDTO> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.isAfter(fim)) {
            throw new PeriodoMovimentacaoInvalidoException(inicio, fim);
        }

        return montarRespostas(movimentacaoEstoqueRepository.findByDataHoraBetweenOrderByDataHoraDesc(inicio, fim));
    }

    public MovimentacaoEstoqueResponseDTO buscarPorId(Integer id) {
        return toResponse(pesquisarPorId(id));
    }

    public MovimentacaoEstoque pesquisarPorId(Integer id) {
        return movimentacaoEstoqueRepository.findById(id)
                .orElseThrow(() -> new MovimentacaoEstoqueNaoEncontradaException(id));
    }

    private ItemEstoque buscarItemParaMovimentacao(Integer id) {
        return itemEstoqueRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(id));
    }

    private Usuario buscarUsuario(String login) {
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(login));
    }

    private OrdemServico buscarOrdemServico(Integer id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(id));
    }

    private void validarItemAtivo(ItemEstoque item) {
        if (!item.isAtivo()) {
            throw new ItemEstoqueInativoException(item.getId());
        }
    }

    private void validarOrdemServicoEmExecucao(OrdemServico ordemServico) {
        if (!StatusOrdemServicoEnum.EM_EXECUCAO.name()
                .equals(ordemServico.getStatusOrdemServico().getNome())) {
            throw new OrdemServicoNaoEstaEmExecucaoException(ordemServico.getId());
        }
    }

    private List<MovimentacaoEstoqueResponseDTO> montarRespostas(List<MovimentacaoEstoque> movimentacoes) {
        Map<Integer, Integer> saldosPorMovimentacao = calcularSaldosHistoricos(movimentacoes);

        return movimentacoes.stream()
                .map(movimentacao -> toResponse(movimentacao, saldosPorMovimentacao.get(movimentacao.getId())))
                .toList();
    }

    private MovimentacaoEstoqueResponseDTO toResponse(MovimentacaoEstoque movimentacao) {
        return toResponse(movimentacao, calcularSaldoHistoricoDoRegistro(movimentacao));
    }

    private Map<Integer, Integer> calcularSaldosHistoricos(List<MovimentacaoEstoque> movimentacoes) {
        Map<Integer, Integer> saldosPorMovimentacao = new HashMap<>();
        Map<Integer, List<MovimentacaoEstoque>> movimentosPorItem = movimentacoes.stream()
                .collect(java.util.stream.Collectors.groupingBy(movimentacao -> movimentacao.getItemEstoque().getId()));

        for (var entry : movimentosPorItem.entrySet()) {
            Integer itemId = entry.getKey();
            List<MovimentacaoEstoque> historico = movimentacaoEstoqueRepository
                    .findByItemEstoque_IdOrderByDataHoraAscIdAsc(itemId);

            int saldo = calcularSaldoInicial(entry.getValue().get(0).getItemEstoque(), historico);
            for (MovimentacaoEstoque movimentoHistorico : historico) {
                if (movimentoHistorico.getTipo() == TipoMovimentacaoEstoque.ENTRADA) {
                    saldo += movimentoHistorico.getQuantidade();
                } else {
                    saldo -= movimentoHistorico.getQuantidade();
                }
                saldosPorMovimentacao.put(movimentoHistorico.getId(), saldo);
            }
        }

        return saldosPorMovimentacao;
    }

    private Integer calcularSaldoHistoricoDoRegistro(MovimentacaoEstoque movimentacao) {
        List<MovimentacaoEstoque> historico = movimentacaoEstoqueRepository
                .findByItemEstoque_IdOrderByDataHoraAscIdAsc(movimentacao.getItemEstoque().getId());

        int saldo = calcularSaldoInicial(movimentacao.getItemEstoque(), historico);
        for (MovimentacaoEstoque movimentoHistorico : historico) {
            if (movimentoHistorico.getTipo() == TipoMovimentacaoEstoque.ENTRADA) {
                saldo += movimentoHistorico.getQuantidade();
            } else {
                saldo -= movimentoHistorico.getQuantidade();
            }

            if (movimentoHistorico.getId().equals(movimentacao.getId())) {
                return saldo;
            }
        }

        return saldo;
    }

    private MovimentacaoEstoqueResponseDTO toResponse(MovimentacaoEstoque movimentacao, Integer saldoHistorico) {
        return new MovimentacaoEstoqueResponseDTO(
                movimentacao.getId(),
                movimentacao.getItemEstoque().getId(),
                movimentacao.getItemEstoque().getNome(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataHora(),
                movimentacao.getUsuario().getId(),
                movimentacao.getOrdemServico() != null ? movimentacao.getOrdemServico().getId() : null,
                saldoHistorico
        );
    }

    private int calcularSaldoInicial(ItemEstoque item, List<MovimentacaoEstoque> historico) {
        int saldoMovimentacoes = historico.stream()
                .mapToInt(movimento -> movimento.getTipo() == TipoMovimentacaoEstoque.ENTRADA
                        ? movimento.getQuantidade()
                        : -movimento.getQuantidade())
                .sum();
        return item.getQuantidadeAtual() - saldoMovimentacoes;
    }
}
