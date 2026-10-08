package com.kap.mechanics_api.core.cliente.usecase;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

public class AtualizarClienteUseCase {

    private final ClienteGateway gateway;

    public AtualizarClienteUseCase(ClienteGateway gateway){
        this.gateway = gateway;
    }

    public Cliente executar(Integer id, String nome, CpfCnpj cpfCnpj, String telefone, String email){
        Cliente cliente = gateway.pesquisarPorId(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
        cliente.atualizar(nome, cpfCnpj, telefone, email);
        return gateway.salvar(cliente);
    }
}
