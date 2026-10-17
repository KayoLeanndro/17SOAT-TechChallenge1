package com.kap.mechanics_api.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;
import com.kap.mechanics_api.core.servico.usecase.AtualizarServicoUseCase;
import com.kap.mechanics_api.core.servico.usecase.BuscarServicoPorIdUseCase;
import com.kap.mechanics_api.core.servico.usecase.CadastrarServicoUseCase;
import com.kap.mechanics_api.core.servico.usecase.ExcluirServicoUseCase;
import com.kap.mechanics_api.core.servico.usecase.ListarServicoUseCase;

@Configuration
public class ServicoUseCaseConfig {

    @Bean
    CadastrarServicoUseCase cadastrarServicoUseCase(ServicoGateway gateway) {
        return new CadastrarServicoUseCase(gateway);
    }

    @Bean
    AtualizarServicoUseCase atualizarServicoUseCase(ServicoGateway gateway) {
        return new AtualizarServicoUseCase(gateway);
    }

    @Bean
    ExcluirServicoUseCase excluirServicoUseCase(ServicoGateway gateway) {
        return new ExcluirServicoUseCase(gateway);
    }

    @Bean
    BuscarServicoPorIdUseCase buscarServicoPorIdUseCase(ServicoGateway gateway) {
        return new BuscarServicoPorIdUseCase(gateway);
    }

    @Bean
    ListarServicoUseCase listarServicoUseCase(ServicoGateway gateway) {
        return new ListarServicoUseCase(gateway);
    }
}
