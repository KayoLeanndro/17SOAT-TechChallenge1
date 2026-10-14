package com.kap.mechanics_api.infra;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;
import com.kap.mechanics_api.core.veiculo.usecase.AtualizarVeiculoUseCase;
import com.kap.mechanics_api.core.veiculo.usecase.BuscarVeiculoPorIdUseCase;
import com.kap.mechanics_api.core.veiculo.usecase.CadastrarVeiculoUseCase;
import com.kap.mechanics_api.core.veiculo.usecase.ExcluirVeiculoUseCase;
import com.kap.mechanics_api.core.veiculo.usecase.ListarVeiculoUseCase;

@Configuration
public class VeiculoUseCaseConfig {

    @Bean
    CadastrarVeiculoUseCase cadastrarVeiculoUseCase(VeiculoGateway veiculoGateway, ClienteGateway clienteGateway, Clock clock) {
        return new CadastrarVeiculoUseCase(veiculoGateway, clienteGateway, clock);
    }

    @Bean
    AtualizarVeiculoUseCase atualizarVeiculoUseCase(VeiculoGateway veiculoGateway, ClienteGateway clienteGateway) {
        return new AtualizarVeiculoUseCase(veiculoGateway, clienteGateway);
    }

    @Bean
    ExcluirVeiculoUseCase excluirVeiculoUseCase(VeiculoGateway gateway) {
        return new ExcluirVeiculoUseCase(gateway);
    }

    @Bean
    BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase(VeiculoGateway gateway) {
        return new BuscarVeiculoPorIdUseCase(gateway);
    }

    @Bean
    ListarVeiculoUseCase listarVeiculoUseCase(VeiculoGateway gateway) {
        return new ListarVeiculoUseCase(gateway);
    }
}
