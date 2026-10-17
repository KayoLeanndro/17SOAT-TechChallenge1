package com.kap.mechanics_api.infra;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.ResponsavelMovimentacaoGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.BaixarItemParaOrdemServicoUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.BaixarItensDaOrdemServicoUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.BuscarMovimentacaoPorIdUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.ListarMovimentacoesPorItemUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.ListarMovimentacoesPorOrdemServicoUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.ListarMovimentacoesPorPeriodoUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.ListarMovimentacoesPorTipoUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.ListarMovimentacoesUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.MontadorDeConsultaDeMovimentacao;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.RegistrarEntradaEstoqueUseCase;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.RegistrarSaidaEstoqueUseCase;

@Configuration
public class MovimentacaoEstoqueUseCaseConfig {

    @Bean
    RegistrarEntradaEstoqueUseCase registrarEntradaEstoqueUseCase(ItemEstoqueGateway itemGateway,
            MovimentacaoEstoqueGateway movimentacaoGateway, ResponsavelMovimentacaoGateway responsavelGateway,
            Clock clock) {
        return new RegistrarEntradaEstoqueUseCase(itemGateway, movimentacaoGateway, responsavelGateway, clock);
    }

    @Bean
    RegistrarSaidaEstoqueUseCase registrarSaidaEstoqueUseCase(ItemEstoqueGateway itemGateway,
            MovimentacaoEstoqueGateway movimentacaoGateway, ResponsavelMovimentacaoGateway responsavelGateway,
            OrdemServicoEstoqueGateway ordemServicoGateway, Clock clock) {
        return new RegistrarSaidaEstoqueUseCase(itemGateway, movimentacaoGateway, responsavelGateway,
                ordemServicoGateway, clock);
    }

    @Bean
    BaixarItemParaOrdemServicoUseCase baixarItemParaOrdemServicoUseCase(ItemEstoqueGateway itemGateway,
            MovimentacaoEstoqueGateway movimentacaoGateway, OrdemServicoEstoqueGateway ordemServicoGateway,
            Clock clock) {
        return new BaixarItemParaOrdemServicoUseCase(itemGateway, movimentacaoGateway, ordemServicoGateway, clock);
    }

    @Bean
    BaixarItensDaOrdemServicoUseCase baixarItensDaOrdemServicoUseCase(OrdemServicoEstoqueGateway ordemServicoGateway,
            BaixarItemParaOrdemServicoUseCase baixarItem) {
        return new BaixarItensDaOrdemServicoUseCase(ordemServicoGateway, baixarItem);
    }

    @Bean
    MontadorDeConsultaDeMovimentacao montadorDeConsultaDeMovimentacao(MovimentacaoEstoqueGateway movimentacaoGateway,
            ItemEstoqueGateway itemGateway) {
        return new MontadorDeConsultaDeMovimentacao(movimentacaoGateway, itemGateway);
    }

    @Bean
    ListarMovimentacoesUseCase listarMovimentacoesUseCase(MovimentacaoEstoqueGateway gateway,
            MontadorDeConsultaDeMovimentacao montador) {
        return new ListarMovimentacoesUseCase(gateway, montador);
    }

    @Bean
    ListarMovimentacoesPorItemUseCase listarMovimentacoesPorItemUseCase(MovimentacaoEstoqueGateway gateway,
            MontadorDeConsultaDeMovimentacao montador) {
        return new ListarMovimentacoesPorItemUseCase(gateway, montador);
    }

    @Bean
    ListarMovimentacoesPorOrdemServicoUseCase listarMovimentacoesPorOrdemServicoUseCase(
            MovimentacaoEstoqueGateway gateway, MontadorDeConsultaDeMovimentacao montador) {
        return new ListarMovimentacoesPorOrdemServicoUseCase(gateway, montador);
    }

    @Bean
    ListarMovimentacoesPorTipoUseCase listarMovimentacoesPorTipoUseCase(MovimentacaoEstoqueGateway gateway,
            MontadorDeConsultaDeMovimentacao montador) {
        return new ListarMovimentacoesPorTipoUseCase(gateway, montador);
    }

    @Bean
    ListarMovimentacoesPorPeriodoUseCase listarMovimentacoesPorPeriodoUseCase(MovimentacaoEstoqueGateway gateway,
            MontadorDeConsultaDeMovimentacao montador) {
        return new ListarMovimentacoesPorPeriodoUseCase(gateway, montador);
    }

    @Bean
    BuscarMovimentacaoPorIdUseCase buscarMovimentacaoPorIdUseCase(MovimentacaoEstoqueGateway gateway,
            MontadorDeConsultaDeMovimentacao montador) {
        return new BuscarMovimentacaoPorIdUseCase(gateway, montador);
    }
}
