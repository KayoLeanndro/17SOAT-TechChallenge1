package com.kap.mechanics_api.core.veiculo.usecase;

import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;
import com.kap.mechanics_api.core.cliente.usecase.ClienteNaoEncontradoException;
import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

public class AtualizarVeiculoUseCase {

    private final VeiculoGateway veiculoGateway;
    private final ClienteGateway clienteGateway;

    public AtualizarVeiculoUseCase(VeiculoGateway veiculoGateway, ClienteGateway clienteGateway) {
        this.veiculoGateway = veiculoGateway;
        this.clienteGateway = clienteGateway;
    }

    public Veiculo executar(Integer id, String placa, String marca, String modelo, Integer ano, Integer clienteId) {
        Veiculo veiculo = veiculoGateway.pesquisarPorId(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException(id));

        if (clienteId != null) {
            clienteGateway.pesquisarPorId(clienteId)
                    .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));
        }

        veiculo.atualizar(placa, marca, modelo, ano);
        Veiculo atualizado = veiculoGateway.salvar(veiculo);

        if (clienteId != null) {
            veiculoGateway.vincularCliente(atualizado.getId(), clienteId);
        }
        return atualizado;
    }
}
