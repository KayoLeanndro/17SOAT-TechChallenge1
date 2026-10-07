package com.kap.mechanics_api.core.cliente.usecase;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

import java.util.List;

public class ListarClienteUseCase {

    private final ClienteGateway gateway;

    public ListarClienteUseCase(ClienteGateway gateway){
        this.gateway= gateway;
    }

    public List<Cliente> executar(){
        return gateway.listarTodos();
    }
}
