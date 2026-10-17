package com.kap.mechanics_api.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.AtualizarItemEstoqueUseCase;
import com.kap.mechanics_api.core.itemestoque.usecase.BuscarItemEstoquePorIdUseCase;
import com.kap.mechanics_api.core.itemestoque.usecase.CadastrarItemEstoqueUseCase;
import com.kap.mechanics_api.core.itemestoque.usecase.ExcluirItemEstoqueUseCase;
import com.kap.mechanics_api.core.itemestoque.usecase.ListarItemEstoqueUseCase;

@Configuration
public class ItemEstoqueUseCaseConfig {

    @Bean
    CadastrarItemEstoqueUseCase cadastrarItemEstoqueUseCase(ItemEstoqueGateway gateway) {
        return new CadastrarItemEstoqueUseCase(gateway);
    }

    @Bean
    AtualizarItemEstoqueUseCase atualizarItemEstoqueUseCase(ItemEstoqueGateway gateway) {
        return new AtualizarItemEstoqueUseCase(gateway);
    }

    @Bean
    ExcluirItemEstoqueUseCase excluirItemEstoqueUseCase(ItemEstoqueGateway gateway) {
        return new ExcluirItemEstoqueUseCase(gateway);
    }

    @Bean
    BuscarItemEstoquePorIdUseCase buscarItemEstoquePorIdUseCase(ItemEstoqueGateway gateway) {
        return new BuscarItemEstoquePorIdUseCase(gateway);
    }

    @Bean
    ListarItemEstoqueUseCase listarItemEstoqueUseCase(ItemEstoqueGateway gateway) {
        return new ListarItemEstoqueUseCase(gateway);
    }
}
