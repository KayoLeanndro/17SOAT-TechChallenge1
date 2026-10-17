package com.kap.mechanics_api.core.itemestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;

public class ExcluirItemEstoqueUseCase {

    private final ItemEstoqueGateway gateway;

    public ExcluirItemEstoqueUseCase(ItemEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public void executar(Integer id) {
        ItemEstoque item = gateway.pesquisarPorId(id)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(id));
        gateway.excluir(item.getId());
    }
}
