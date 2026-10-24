package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.StatusOrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

/**
 * Reação à aprovação do orçamento: a ordem sai de "aguardando aprovação" para diagnóstico.
 */
public class IniciarDiagnosticoPorOrcamentoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final TransicionarStatusOrdemServicoUseCase transicionar;

    public IniciarDiagnosticoPorOrcamentoUseCase(OrdemServicoGateway ordemServicoGateway,
                                                 TransicionarStatusOrdemServicoUseCase transicionar) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.transicionar = transicionar;
    }

    public OrdemServico executar(Integer orcamentoId) {
        OrdemServico ordemServico = ordemServicoGateway.pesquisarPorOrcamentoId(orcamentoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(orcamentoId));
        return transicionar.executar(ordemServico.getId(), StatusOrdemServico.EM_DIAGNOSTICO);
    }
}
