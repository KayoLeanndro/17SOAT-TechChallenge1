package com.kap.mechanics_api.core.cliente.usecase;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

public class BuscarClientePorIdUseCase {

    private final ClienteGateway gateway;

    public BuscarClientePorIdUseCase(ClienteGateway gateway){
        this.gateway= gateway;
    }

    public Cliente executar(Integer id){
        return gateway.pesquisarPorId(id).orElseThrow( () -> new ClienteNaoEncontradoException(id));
    }

}
