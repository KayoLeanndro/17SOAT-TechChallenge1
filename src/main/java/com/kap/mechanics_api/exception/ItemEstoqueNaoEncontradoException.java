package com.kap.mechanics_api.exception;

public class ItemEstoqueNaoEncontradoException extends RuntimeException {

    public ItemEstoqueNaoEncontradoException(Integer id) {
        super("Item de estoque não encontrado com o id " + id);
    }
}
