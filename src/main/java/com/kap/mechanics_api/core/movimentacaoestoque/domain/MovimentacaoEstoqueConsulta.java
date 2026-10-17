package com.kap.mechanics_api.core.movimentacaoestoque.domain;

/**
 * Resultado das consultas de movimentação: o lançamento, o nome do item e o saldo do item
 * logo após esse lançamento.
 */
public record MovimentacaoEstoqueConsulta(MovimentacaoEstoque movimentacao, String itemEstoqueNome,
                                          Integer saldoItemEstoque) {
}
