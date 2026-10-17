package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;

import java.time.LocalDateTime;
import java.util.List;

public class ListarMovimentacoesPorPeriodoUseCase {

    private final MovimentacaoEstoqueGateway gateway;
    private final MontadorDeConsultaDeMovimentacao montador;

    public ListarMovimentacoesPorPeriodoUseCase(MovimentacaoEstoqueGateway gateway,
                                                MontadorDeConsultaDeMovimentacao montador) {
        this.gateway = gateway;
        this.montador = montador;
    }

    public List<MovimentacaoEstoqueConsulta> executar(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.isAfter(fim)) {
            throw new PeriodoMovimentacaoInvalidoException(inicio, fim);
        }
        return montador.montar(gateway.listarPorPeriodo(inicio, fim));
    }
}
