package com.kap.mechanics_api.core.cliente.usecase;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

public class ExcluirClienteUseCase {

    private final ClienteGateway gateway;

    public ExcluirClienteUseCase(ClienteGateway gateway){
        this.gateway = gateway;
    }

    public void executar(Integer id){
        Cliente cliente = gateway.pesquisarPorId(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
        gateway.excluir(cliente.getId());
    }

}
