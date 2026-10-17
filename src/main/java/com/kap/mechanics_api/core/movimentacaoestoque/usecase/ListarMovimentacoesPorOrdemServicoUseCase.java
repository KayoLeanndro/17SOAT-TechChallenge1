package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;

import java.util.List;

public class ListarMovimentacoesPorOrdemServicoUseCase {

    private final MovimentacaoEstoqueGateway gateway;
    private final MontadorDeConsultaDeMovimentacao montador;

    public ListarMovimentacoesPorOrdemServicoUseCase(MovimentacaoEstoqueGateway gateway,
                                                     MontadorDeConsultaDeMovimentacao montador) {
        this.gateway = gateway;
        this.montador = montador;
    }

    public List<MovimentacaoEstoqueConsulta> executar(Integer ordemServicoId) {
        return montador.montar(gateway.listarPorOrdemServico(ordemServicoId));
    }
}
