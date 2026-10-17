package com.kap.mechanics_api.core.itemestoque.domain;

public class ItemEstoqueInativoException extends RuntimeException {

    public ItemEstoqueInativoException(Integer itemEstoqueId) {
        super("O item de estoque " + itemEstoqueId + " esta inativo.");
    }
}
