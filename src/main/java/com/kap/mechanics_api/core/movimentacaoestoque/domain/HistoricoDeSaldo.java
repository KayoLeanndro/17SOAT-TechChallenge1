package com.kap.mechanics_api.core.movimentacaoestoque.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HistoricoDeSaldo {

    private HistoricoDeSaldo() {
    }

    /**
     * Reconstrói o saldo do item depois de cada lançamento.
     *
     * @param quantidadeAtual saldo atual do item
     * @param historico       todos os lançamentos do item em ordem cronológica (data e id crescentes)
     * @return saldo do item logo após cada lançamento, indexado pelo id do lançamento
     */
    public static Map<Integer, Integer> saldoAposCadaMovimentacao(int quantidadeAtual,
                                                                  List<MovimentacaoEstoque> historico) {
        int saldo = quantidadeAtual - historico.stream().mapToInt(MovimentacaoEstoque::variacaoDeSaldo).sum();

        Map<Integer, Integer> saldos = new HashMap<>();
        for (MovimentacaoEstoque movimentacao : historico) {
            saldo += movimentacao.variacaoDeSaldo();
            saldos.put(movimentacao.getId(), saldo);
        }
        return saldos;
    }
}
