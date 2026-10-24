package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.AtendenteGateway;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Parte comum dos dois jeitos de abrir uma ordem (para orçamento pendente ou já aprovado):
 * resolver o atendente, impedir ordem duplicada e gravar a ordem recém-aberta.
 */
public class AbridorDeOrdemServico {

    private final OrdemServicoGateway ordemServicoGateway;
    private final AtendenteGateway atendenteGateway;
    private final Clock clock;

    public AbridorDeOrdemServico(OrdemServicoGateway ordemServicoGateway, AtendenteGateway atendenteGateway,
                                 Clock clock) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.atendenteGateway = atendenteGateway;
        this.clock = clock;
    }

    public Integer buscarAtendente(String login) {
        return atendenteGateway.buscarIdPorLogin(login)
                .orElseThrow(() -> new AtendenteNaoEncontradoException(login));
    }

    public OrdemServico abrir(Integer orcamentoId, Integer atendenteId) {
        if (ordemServicoGateway.existePorOrcamentoId(orcamentoId)) {
            throw new OrdemServicoJaExisteException("Este orçamento já possui uma OS gerada");
        }
        return ordemServicoGateway.salvar(OrdemServico.abrir(orcamentoId, atendenteId, LocalDateTime.now(clock)));
    }
}
