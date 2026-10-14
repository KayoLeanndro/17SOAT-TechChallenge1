package com.kap.mechanics_api.core.veiculo.usecase;

import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

public class BuscarVeiculoPorIdUseCase {

    private final VeiculoGateway gateway;

    public BuscarVeiculoPorIdUseCase(VeiculoGateway gateway) {
        this.gateway = gateway;
    }

    public Veiculo executar(Integer id) {
        return gateway.pesquisarPorId(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException(id));
    }
}
