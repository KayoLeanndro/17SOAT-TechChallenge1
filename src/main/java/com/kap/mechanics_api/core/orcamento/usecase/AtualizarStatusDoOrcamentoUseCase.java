package com.kap.mechanics_api.core.orcamento.usecase;

import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.ordemservico.usecase.FinalizarOrdemServicoPorOrcamentoUseCase;
import com.kap.mechanics_api.core.ordemservico.usecase.IniciarDiagnosticoPorOrcamentoUseCase;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Registra a resposta do cliente. Aprovado leva a ordem de serviço ao diagnóstico; rejeitado a encerra.
 * Quem chama deve manter tudo na mesma transação.
 */
public class AtualizarStatusDoOrcamentoUseCase {

    private final OrcamentoGateway orcamentoGateway;
    private final IniciarDiagnosticoPorOrcamentoUseCase iniciarDiagnostico;
    private final FinalizarOrdemServicoPorOrcamentoUseCase finalizarOrdemServico;
    private final Clock clock;

    public AtualizarStatusDoOrcamentoUseCase(OrcamentoGateway orcamentoGateway,
                                             IniciarDiagnosticoPorOrcamentoUseCase iniciarDiagnostico,
                                             FinalizarOrdemServicoPorOrcamentoUseCase finalizarOrdemServico,
                                             Clock clock) {
        this.orcamentoGateway = orcamentoGateway;
        this.iniciarDiagnostico = iniciarDiagnostico;
        this.finalizarOrdemServico = finalizarOrdemServico;
        this.clock = clock;
    }

    public void executar(Integer orcamentoId, String status) {
        Orcamento orcamento = orcamentoGateway.pesquisarPorId(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"));
        StatusOrcamento novoStatus = StatusOrcamento.converter(status);

        orcamento.alterarStatus(novoStatus, LocalDateTime.now(clock));
        orcamentoGateway.salvar(orcamento);

        if (novoStatus == StatusOrcamento.APROVADO) {
            iniciarDiagnostico.executar(orcamentoId);
        } else if (novoStatus == StatusOrcamento.REJEITADO) {
            finalizarOrdemServico.executar(orcamentoId);
        }
    }
}
