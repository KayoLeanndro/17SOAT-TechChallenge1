package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrcamentoServico;
import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.domain.ServicoItem;
import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;

import com.kap.mechanics_api.exception.OrcamentoNaoEncontradoException;
import com.kap.mechanics_api.exception.StatusOrcamentoInvalidoException;

import com.kap.mechanics_api.exception.OrcamentoJaRespondidoException;
import com.kap.mechanics_api.exception.OrcamentoNaoEncontradoException;

import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    private final ServicoService servicoService;
    private final ServicoItemRepository servicoItemRepository;
    private final OrcamentoServicoRepository orcamentoServicoRepository;
    private final OrdemServicoService ordemServicoService;

    public OrcamentoService(OrcamentoRepository orcamentoRepository,
                            ClienteService clienteService,
                            VeiculoService veiculoService,
                            ServicoService servicoService,
                            ServicoItemRepository servicoItemRepository,
                            OrcamentoServicoRepository orcamentoServicoRepository,
                            OrdemServicoService ordemServicoService) {
        this.orcamentoRepository = orcamentoRepository;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.servicoService = servicoService;
        this.servicoItemRepository = servicoItemRepository;
        this.orcamentoServicoRepository = orcamentoServicoRepository;
        this.ordemServicoService = ordemServicoService;
    }

    @Transactional
    public void gerarOrcamento(GeracaoOrcamentoRequestDTO dto, String usuarioLogin) {
        Cliente cliente = clienteService.pesquisarPorId(dto.clienteId());
        Veiculo veiculo = veiculoService.pesquisarPorId(dto.veiculoId());

        Orcamento orcamento = new Orcamento();
        orcamento.setCliente(cliente);
        orcamento.setVeiculo(veiculo);
        orcamento.setDataCriacao(LocalDateTime.now());
        orcamento.setStatusOrcamento(StatusOrcamento.PENDENTE);
        orcamento.setValorTotal(BigDecimal.ZERO);
        orcamento = orcamentoRepository.save(orcamento);

        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Integer servicoId : dto.servicosIds()) {
            Servico servico = servicoService.pesquisarPorId(servicoId);
            BigDecimal valorItens = calcularValorItens(servico.getId());
            BigDecimal valorServico = servico.getValorMaoDeObra().add(valorItens);

            valorTotal = valorTotal.add(valorServico);
            orcamentoServicoRepository.save(new OrcamentoServico(orcamento, servico, valorServico));
        }

        orcamento.setValorTotal(valorTotal);
        orcamentoRepository.save(orcamento);
        ordemServicoService.criarParaOrcamentoPendente(orcamento.getId(), usuarioLogin);
    }

    @Transactional
    public void responder(Integer orcamentoId, StatusOrcamento novoStatus) {
        if (novoStatus == StatusOrcamento.PENDENTE) {
            throw new IllegalArgumentException("O orçamento só pode ser aprovado ou rejeitado.");
        }

        Orcamento orcamento = pesquisarPorId(orcamentoId);
        if (orcamento.getStatusOrcamento() != StatusOrcamento.PENDENTE) {
            throw new OrcamentoJaRespondidoException(orcamentoId);
        }

        orcamento.setStatusOrcamento(novoStatus);
        orcamento.setDataResposta(LocalDateTime.now());
        orcamentoRepository.save(orcamento);

        if (novoStatus == StatusOrcamento.APROVADO) {
            ordemServicoService.iniciarDiagnosticoPorOrcamento(orcamentoId);
        } else {
            ordemServicoService.finalizarPorOrcamento(orcamentoId);
        }
    }

    public Orcamento pesquisarPorId(Integer id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"));
    }

    private BigDecimal calcularValorItens(Integer servicoId) {
        List<ServicoItem> itensDoServico = servicoItemRepository.findByServico_Id(servicoId);
        return itensDoServico.stream()
                .map(item -> item.getItemEstoque().getValorUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidadePadrao())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Orcamento pesquisarPorId(Integer id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(id));
    }

    @Transactional
    public void atualizarStatus(Integer id, String status) {
        Orcamento orcamento = pesquisarPorId(id);
        StatusOrcamento novoStatus = converterStatus(status);

        orcamento.setStatusOrcamento(novoStatus);

        if (StatusOrcamento.APROVADO.equals(novoStatus) || StatusOrcamento.REJEITADO.equals(novoStatus)) {
            orcamento.setDataResposta(LocalDateTime.now());
        }

        orcamentoRepository.save(orcamento);
    }

    private StatusOrcamento converterStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new StatusOrcamentoInvalidoException(status);
        }

        try {
            return StatusOrcamento.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new StatusOrcamentoInvalidoException(status);
        }
    }
}
