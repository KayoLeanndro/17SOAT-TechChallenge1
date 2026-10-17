package com.kap.mechanics_api.core.itemestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;

public class BuscarItemEstoquePorIdUseCase {

    private final ItemEstoqueGateway gateway;

    public BuscarItemEstoquePorIdUseCase(ItemEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public ItemEstoque executar(Integer id) {
        return gateway.pesquisarPorId(id)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(id));
    }
}
