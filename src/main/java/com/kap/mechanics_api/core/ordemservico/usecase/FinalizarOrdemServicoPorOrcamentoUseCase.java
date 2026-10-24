package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Reação à rejeição do orçamento: a ordem é encerrada direto, sem passar pelo fluxo normal.
 */
public class FinalizarOrdemServicoPorOrcamentoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final Clock clock;

    public FinalizarOrdemServicoPorOrcamentoUseCase(OrdemServicoGateway ordemServicoGateway, Clock clock) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.clock = clock;
    }

    public OrdemServico executar(Integer orcamentoId) {
        OrdemServico ordemServico = ordemServicoGateway.pesquisarPorOrcamentoId(orcamentoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(orcamentoId));
        ordemServico.finalizarPorOrcamento(LocalDateTime.now(clock));
        return ordemServicoGateway.salvar(ordemServico);
    }
}
