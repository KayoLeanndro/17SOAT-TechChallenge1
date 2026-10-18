package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.ordemservico.domain.HistoricoDeStatus;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

import java.util.Comparator;
import java.util.List;

public class ConsultarHistoricoDeStatusUseCase {

    private final OrdemServicoGateway gateway;

    public ConsultarHistoricoDeStatusUseCase(OrdemServicoGateway gateway) {
        this.gateway = gateway;
    }

    // Do mais antigo para o mais recente.
    public List<HistoricoDeStatus> executar(Integer ordemServicoId) {
        return gateway.pesquisarPorId(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId))
                .getHistorico().stream()
                .sorted(Comparator.comparing(HistoricoDeStatus::getInicio))
                .toList();
    }
}
