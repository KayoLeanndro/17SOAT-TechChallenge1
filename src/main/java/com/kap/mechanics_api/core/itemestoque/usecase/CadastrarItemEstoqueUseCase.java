package com.kap.mechanics_api.core.itemestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;

import java.math.BigDecimal;

public class CadastrarItemEstoqueUseCase {

    private final ItemEstoqueGateway gateway;

    public CadastrarItemEstoqueUseCase(ItemEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public ItemEstoque executar(String nome, String descricao, TipoItemEstoque tipoItemEstoque,
                                BigDecimal valorUnitario, int quantidadeMinima, boolean ativo) {
        return gateway.salvar(ItemEstoque.novo(nome, descricao, tipoItemEstoque, valorUnitario,
                quantidadeMinima, ativo));
    }
}
