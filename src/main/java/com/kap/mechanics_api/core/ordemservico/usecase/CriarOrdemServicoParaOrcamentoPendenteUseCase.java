package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.orcamento.usecase.OrcamentoNaoEncontradoException;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;

/**
 * OS inicial, aberta junto com o orçamento enquanto ele ainda aguarda a resposta do cliente.
 */
public class CriarOrdemServicoParaOrcamentoPendenteUseCase {

    private final OrcamentoGateway orcamentoGateway;
    private final AbridorDeOrdemServico abridor;

    public CriarOrdemServicoParaOrcamentoPendenteUseCase(OrcamentoGateway orcamentoGateway,
                                                         AbridorDeOrdemServico abridor) {
        this.orcamentoGateway = orcamentoGateway;
        this.abridor = abridor;
    }

    public OrdemServico executar(Integer orcamentoId, String atendenteLogin) {
        Integer atendenteId = abridor.buscarAtendente(atendenteLogin);
        Orcamento orcamento = orcamentoGateway.pesquisarPorId(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"));

        if (orcamento.getStatus() != StatusOrcamento.PENDENTE) {
            throw new IllegalArgumentException("A OS inicial só pode ser criada para orçamento pendente.");
        }
        return abridor.abrir(orcamentoId, atendenteId);
    }
}
