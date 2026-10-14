package com.kap.mechanics_api.core.veiculo.usecase;

import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

import java.util.List;

public class ListarVeiculoUseCase {

    private final VeiculoGateway gateway;

    public ListarVeiculoUseCase(VeiculoGateway gateway) {
        this.gateway = gateway;
    }

    public List<Veiculo> executar() {
        return gateway.listarTodos();
    }
}
