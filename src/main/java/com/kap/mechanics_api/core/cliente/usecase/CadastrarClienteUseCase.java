package com.kap.mechanics_api.core.cliente.usecase;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

import java.time.Clock;
import java.time.LocalDateTime;

public class CadastrarClienteUseCase {


    private final ClienteGateway gateway;
    private final Clock clock;

    public CadastrarClienteUseCase(ClienteGateway gateway, Clock clock){
        this.gateway = gateway;
        this.clock= clock;
    }

    public Cliente executar(String nome, CpfCnpj cpfCnpj, String telefone, String email){
        return gateway.salvar(Cliente.novo(nome,cpfCnpj, telefone,email, LocalDateTime.now(clock)));
    }

}
