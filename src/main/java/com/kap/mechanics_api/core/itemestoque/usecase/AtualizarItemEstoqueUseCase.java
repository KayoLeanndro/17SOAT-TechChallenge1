package com.kap.mechanics_api.core.itemestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;

import java.math.BigDecimal;

public class AtualizarItemEstoqueUseCase {

    private final ItemEstoqueGateway gateway;

    public AtualizarItemEstoqueUseCase(ItemEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public ItemEstoque executar(Integer id, String nome, String descricao, TipoItemEstoque tipoItemEstoque,
                                BigDecimal valorUnitario, Integer quantidadeMinima, Boolean ativo) {
        ItemEstoque item = gateway.pesquisarPorId(id)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(id));

        item.atualizar(nome, descricao, tipoItemEstoque, valorUnitario, quantidadeMinima, ativo);
        return gateway.salvar(item);
    }
}
