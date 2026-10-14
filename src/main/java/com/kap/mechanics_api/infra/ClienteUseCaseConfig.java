package com.kap.mechanics_api.infra;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;
import com.kap.mechanics_api.core.cliente.usecase.AtualizarClienteUseCase;
import com.kap.mechanics_api.core.cliente.usecase.BuscarClientePorDocumentoUseCase;
import com.kap.mechanics_api.core.cliente.usecase.BuscarClientePorIdUseCase;
import com.kap.mechanics_api.core.cliente.usecase.CadastrarClienteUseCase;
import com.kap.mechanics_api.core.cliente.usecase.ExcluirClienteUseCase;
import com.kap.mechanics_api.core.cliente.usecase.ListarClienteUseCase;

@Configuration
public class ClienteUseCaseConfig {

    @Bean
    CadastrarClienteUseCase cadastrarClienteUseCase(ClienteGateway gateway, Clock clock) {
        return new CadastrarClienteUseCase(gateway, clock);
    }

    @Bean
    AtualizarClienteUseCase atualizarClienteUseCase(ClienteGateway gateway) {
        return new AtualizarClienteUseCase(gateway);
    }

    @Bean
    ExcluirClienteUseCase excluirClienteUseCase(ClienteGateway gateway) {
        return new ExcluirClienteUseCase(gateway);
    }

    @Bean
    BuscarClientePorIdUseCase buscarClientePorIdUseCase(ClienteGateway gateway) {
        return new BuscarClientePorIdUseCase(gateway);
    }

    @Bean
    BuscarClientePorDocumentoUseCase buscarClientePorDocumentoUseCase(ClienteGateway gateway) {
        return new BuscarClientePorDocumentoUseCase(gateway);
    }

    @Bean
    ListarClienteUseCase listarClienteUseCase(ClienteGateway gateway) {
        return new ListarClienteUseCase(gateway);
    }
}
