package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.orcamento.usecase.OrcamentoNaoEncontradoException;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;

/**
 * Abre a ordem de serviço de um orçamento que o cliente já aprovou.
 */
public class GerarOrdemServicoUseCase {

    private final OrcamentoGateway orcamentoGateway;
    private final AbridorDeOrdemServico abridor;

    public GerarOrdemServicoUseCase(OrcamentoGateway orcamentoGateway, AbridorDeOrdemServico abridor) {
        this.orcamentoGateway = orcamentoGateway;
        this.abridor = abridor;
    }

    public OrdemServico executar(Integer orcamentoId, String atendenteLogin) {
        Integer atendenteId = abridor.buscarAtendente(atendenteLogin);
        Orcamento orcamento = orcamentoGateway.pesquisarPorId(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"));

        if (orcamento.getStatus() != StatusOrcamento.APROVADO) {
            throw new OrcamentoNaoAprovadoException("Orçamento precisa estar aprovado para gerar OS");
        }
        return abridor.abrir(orcamentoId, atendenteId);
    }
}
