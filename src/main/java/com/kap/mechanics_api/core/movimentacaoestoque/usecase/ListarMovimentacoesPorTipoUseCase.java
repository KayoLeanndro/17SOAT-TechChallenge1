package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;

import java.util.List;

public class ListarMovimentacoesPorTipoUseCase {

    private final MovimentacaoEstoqueGateway gateway;
    private final MontadorDeConsultaDeMovimentacao montador;

    public ListarMovimentacoesPorTipoUseCase(MovimentacaoEstoqueGateway gateway,
                                             MontadorDeConsultaDeMovimentacao montador) {
        this.gateway = gateway;
        this.montador = montador;
    }

    public List<MovimentacaoEstoqueConsulta> executar(TipoMovimentacaoEstoque tipo) {
        return montador.montar(gateway.listarPorTipo(tipo));
    }
}
