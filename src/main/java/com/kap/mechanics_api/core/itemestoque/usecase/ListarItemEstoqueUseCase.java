package com.kap.mechanics_api.core.itemestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;

import java.util.List;

public class ListarItemEstoqueUseCase {

    private final ItemEstoqueGateway gateway;

    public ListarItemEstoqueUseCase(ItemEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public List<ItemEstoque> executar() {
        return gateway.listarTodos();
    }
}
