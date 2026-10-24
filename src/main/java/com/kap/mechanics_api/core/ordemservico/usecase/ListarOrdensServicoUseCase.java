package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.ordemservico.domain.OrdemServicoResumo;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

import java.util.List;

public class ListarOrdensServicoUseCase {

    private final OrdemServicoGateway gateway;

    public ListarOrdensServicoUseCase(OrdemServicoGateway gateway) {
        this.gateway = gateway;
    }

    public List<OrdemServicoResumo> executar() {
        return gateway.listarResumos();
    }
}
