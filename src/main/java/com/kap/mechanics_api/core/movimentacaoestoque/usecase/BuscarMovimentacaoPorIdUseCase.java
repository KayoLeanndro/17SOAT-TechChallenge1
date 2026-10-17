package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;

public class BuscarMovimentacaoPorIdUseCase {

    private final MovimentacaoEstoqueGateway gateway;
    private final MontadorDeConsultaDeMovimentacao montador;

    public BuscarMovimentacaoPorIdUseCase(MovimentacaoEstoqueGateway gateway,
                                          MontadorDeConsultaDeMovimentacao montador) {
        this.gateway = gateway;
        this.montador = montador;
    }

    public MovimentacaoEstoqueConsulta executar(Integer id) {
        return montador.montar(gateway.pesquisarPorId(id)
                .orElseThrow(() -> new MovimentacaoEstoqueNaoEncontradaException(id)));
    }
}
