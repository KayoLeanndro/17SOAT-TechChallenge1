package com.kap.mechanics_api.core.veiculo.usecase;

import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

public class ExcluirVeiculoUseCase {

    private final VeiculoGateway gateway;

    public ExcluirVeiculoUseCase(VeiculoGateway gateway) {
        this.gateway = gateway;
    }

    public void executar(Integer id) {
        Veiculo veiculo = gateway.pesquisarPorId(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException(id));
        gateway.excluir(veiculo.getId());
    }
}
