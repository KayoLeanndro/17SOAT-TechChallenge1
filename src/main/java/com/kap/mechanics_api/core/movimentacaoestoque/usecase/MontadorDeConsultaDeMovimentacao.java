package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.HistoricoDeSaldo;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enriquece os lançamentos com o nome do item e o saldo histórico. Compartilhado pelos casos
 * de uso de consulta.
 */
public class MontadorDeConsultaDeMovimentacao {

    private final MovimentacaoEstoqueGateway movimentacaoGateway;
    private final ItemEstoqueGateway itemGateway;

    public MontadorDeConsultaDeMovimentacao(MovimentacaoEstoqueGateway movimentacaoGateway,
                                            ItemEstoqueGateway itemGateway) {
        this.movimentacaoGateway = movimentacaoGateway;
        this.itemGateway = itemGateway;
    }

    public List<MovimentacaoEstoqueConsulta> montar(List<MovimentacaoEstoque> movimentacoes) {
        Map<Integer, ItemEstoque> itens = new HashMap<>();
        Map<Integer, Integer> saldos = new HashMap<>();

        movimentacoes.stream()
                .map(MovimentacaoEstoque::getItemEstoqueId)
                .distinct()
                .forEach(itemId -> {
                    ItemEstoque item = itemGateway.pesquisarPorId(itemId)
                            .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(itemId));
                    itens.put(itemId, item);
                    saldos.putAll(HistoricoDeSaldo.saldoAposCadaMovimentacao(
                            item.getQuantidadeAtual(), movimentacaoGateway.listarHistoricoDoItem(itemId)));
                });

        return movimentacoes.stream()
                .map(m -> new MovimentacaoEstoqueConsulta(
                        m, itens.get(m.getItemEstoqueId()).getNome(), saldos.get(m.getId())))
                .collect(Collectors.toList());
    }

    public MovimentacaoEstoqueConsulta montar(MovimentacaoEstoque movimentacao) {
        return montar(List.of(movimentacao)).get(0);
    }
}
