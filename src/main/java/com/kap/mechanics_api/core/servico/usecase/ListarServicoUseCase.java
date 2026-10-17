package com.kap.mechanics_api.core.servico.usecase;

import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

import java.util.List;

public class ListarServicoUseCase {

    private final ServicoGateway gateway;

    public ListarServicoUseCase(ServicoGateway gateway) {
        this.gateway = gateway;
    }

    public List<Servico> executar() {
        return gateway.listarTodos();
    }
}
