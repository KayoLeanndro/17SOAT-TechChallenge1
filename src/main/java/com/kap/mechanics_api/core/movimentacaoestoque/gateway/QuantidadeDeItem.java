package com.kap.mechanics_api.core.movimentacaoestoque.gateway;

/**
 * @param itemEstoqueId nulo nas linhas da ordem que não são de estoque (as de serviço)
 */
public record QuantidadeDeItem(Integer itemEstoqueId, int quantidade) {
}
