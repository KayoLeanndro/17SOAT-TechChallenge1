package com.kap.mechanics_api.core.veiculo.usecase;

import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;
import com.kap.mechanics_api.core.cliente.usecase.ClienteNaoEncontradoException;
import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

import java.time.Clock;
import java.time.LocalDateTime;

public class CadastrarVeiculoUseCase {

    private final VeiculoGateway veiculoGateway;
    private final ClienteGateway clienteGateway;
    private final Clock clock;

    public CadastrarVeiculoUseCase(VeiculoGateway veiculoGateway, ClienteGateway clienteGateway, Clock clock) {
        this.veiculoGateway = veiculoGateway;
        this.clienteGateway = clienteGateway;
        this.clock = clock;
    }

    public Veiculo executar(String placa, String marca, String modelo, Integer ano, Integer clienteId) {
        // valida o cliente antes de gravar qualquer coisa
        clienteGateway.pesquisarPorId(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));

        Veiculo veiculo = veiculoGateway.salvar(
                Veiculo.novo(placa, marca, modelo, ano, LocalDateTime.now(clock)));
        veiculoGateway.vincularCliente(veiculo.getId(), clienteId);
        return veiculo;
    }
}
