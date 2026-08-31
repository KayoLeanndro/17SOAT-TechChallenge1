package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.HistoricoStatusOs;
import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.dto.ordemservico.ListagemOrdemServicoResponseDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.exception.OrcamentoNaoAprovadoException;
import com.kap.mechanics_api.exception.OrcamentoNaoEncontradoException;
import com.kap.mechanics_api.exception.ClienteNaoEncontradoException;
import com.kap.mechanics_api.exception.OrdemServicoJaExisteException;
import com.kap.mechanics_api.exception.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.exception.UsuarioNaoEncontradoException;
import com.kap.mechanics_api.mapper.OrdemServicoMapper;
import com.kap.mechanics_api.repository.ClienteRepository;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.HistoricoStatusOsRepository;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import com.kap.mechanics_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final StatusOrdemServicoRepository statusOrdemServicoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final TransicaoStatusOrdemServico transicaoStatusOrdemServico;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoStatusOsRepository historicoStatusOsRepository;
    private final ClienteRepository clienteRepository;
    private final OrdemServicoMapper ordemServicoMapper;


    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               StatusOrdemServicoRepository statusOrdemServicoRepository,
                               OrcamentoRepository orcamentoRepository,
                               TransicaoStatusOrdemServico transicaoStatusOrdemServico,
                               UsuarioRepository usuarioRepository,
                               HistoricoStatusOsRepository historicoStatusOsRepository,
                               ClienteRepository clienteRepository,
                               OrdemServicoMapper ordemServicoMapper) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.statusOrdemServicoRepository = statusOrdemServicoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.transicaoStatusOrdemServico = transicaoStatusOrdemServico;
        this.usuarioRepository = usuarioRepository;
        this.historicoStatusOsRepository = historicoStatusOsRepository;
        this.clienteRepository = clienteRepository;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    @Transactional
    public OrdemServico gerarOrdemServico(Integer orcamentoId, String usuarioLogin) {
        Usuario usuario = usuarioRepository.findByLogin(usuarioLogin)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioLogin));
        return gerarOrdemServico(orcamentoId, usuario);
    }

    @Transactional
    public OrdemServico criarParaOrcamentoPendente(Integer orcamentoId, String usuarioLogin) {
        Usuario usuario = usuarioRepository.findByLogin(usuarioLogin)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioLogin));
        Orcamento orcamento = buscarOrcamento(orcamentoId);

        if (orcamento.getStatusOrcamento() != StatusOrcamento.PENDENTE) {
            throw new IllegalArgumentException("A OS inicial só pode ser criada para orçamento pendente.");
        }
        return criarOrdemServico(orcamento, usuario);
    }

    @Transactional
    public OrdemServico gerarOrdemServico(Integer orcamentoId, Usuario usuario) {
        Orcamento orcamento = buscarOrcamento(orcamentoId);

        if (orcamento.getStatusOrcamento() != StatusOrcamento.APROVADO) {
            throw new OrcamentoNaoAprovadoException("Orçamento precisa estar aprovado para gerar OS");
        }
        return criarOrdemServico(orcamento, usuario);
    }

    @Transactional
    public OrdemServico finalizarPorOrcamento(Integer orcamentoId) {
        OrdemServico ordemServico = ordemServicoRepository.findByOrcamento_Id(orcamentoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(orcamentoId));
        transicaoStatusOrdemServico.finalizarPorOrcamento(ordemServico);
        return ordemServicoRepository.save(ordemServico);
    }

    @Transactional
    public OrdemServico iniciarDiagnosticoPorOrcamento(Integer orcamentoId) {
        OrdemServico ordemServico = ordemServicoRepository.findByOrcamento_Id(orcamentoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(orcamentoId));
        return transicionarStatus(ordemServico.getId(), StatusOrdemServicoEnum.EM_DIAGNOSTICO);
    }

    private OrdemServico criarOrdemServico(Orcamento orcamento, Usuario usuario) {
        if (ordemServicoRepository.existsByOrcamentoId(orcamento.getId())) {
            throw new OrdemServicoJaExisteException("Este orçamento já possui uma OS gerada");
        }

        StatusOrdemServico statusInicial = statusOrdemServicoRepository
                .findByNome(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name())
                .orElseThrow(() -> new IllegalStateException("Status AGUARDANDO_APROVACAO não cadastrado"));

        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setOrcamento(orcamento);
        ordemServico.setUsuarioAtendente(usuario);
        ordemServico.setStatusOrdemServico(statusInicial);
        LocalDateTime dataAbertura = LocalDateTime.now();
        ordemServico.setDataAbertura(dataAbertura);

        OrdemServico ordemServicoSalva = ordemServicoRepository.save(ordemServico);
        historicoStatusOsRepository.save(
                new HistoricoStatusOs(ordemServicoSalva, statusInicial, dataAbertura));
        return ordemServicoSalva;
    }

    private Orcamento buscarOrcamento(Integer orcamentoId) {
        return orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"));
    }

    @Transactional
    public OrdemServico transicionarStatus(Integer ordemServicoId, StatusOrdemServicoEnum novoStatus) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));

        transicaoStatusOrdemServico.transicionar(ordemServico, novoStatus);
        return ordemServicoRepository.save(ordemServico);
    }

    public List<ListagemOrdemServicoResponseDTO> listarPorCliente(Integer clienteId, String cpfCnpj) {
        Integer idCliente = obterIdCliente(clienteId, cpfCnpj);

        return ordemServicoRepository.findByOrcamento_Cliente_IdOrderByDataAberturaDesc(idCliente)
                .stream()
                .map(ordemServico -> new ListagemOrdemServicoResponseDTO(
                        ordemServico.getId(),
                        ordemServico.getOrcamento().getId(),
                        ordemServico.getStatusOrdemServico().getNome(),
                        ordemServico.getDataAbertura(),
                        ordemServico.getDataEntrega()))
                .toList();
    }

    private Integer obterIdCliente(Integer clienteId, String cpfCnpj) {
        if (clienteId != null && cpfCnpj != null && !cpfCnpj.isBlank()) {
            throw new IllegalArgumentException("Informe apenas o ID do cliente ou CPF/CNPJ");
        }

        if (clienteId != null) {
            return clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId))
                    .getId();
        }

        if (cpfCnpj == null || cpfCnpj.isBlank()) {
            throw new IllegalArgumentException("Informe o ID do cliente ou CPF/CNPJ");
        }

        if (!cpfCnpj.matches("[\\d.\\-/\\s]+")) {
            throw new IllegalArgumentException("O documento contém caracteres inválidos");
        }

        String documento = cpfCnpj.trim().replaceAll("\\D", "");
        if (documento.length() != 11 && documento.length() != 14) {
            throw new IllegalArgumentException("O documento deve possuir 11 dígitos para CPF ou 14 para CNPJ");
        }

        return clienteRepository.findByCpfCnpj(documento)
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj))
                .getId();
    }

    public List<ListagemOrdemServicoResponseDTO> listar() {
        return ordemServicoMapper.toListagemResponseDtoList(ordemServicoRepository.findAll());
    }
}
