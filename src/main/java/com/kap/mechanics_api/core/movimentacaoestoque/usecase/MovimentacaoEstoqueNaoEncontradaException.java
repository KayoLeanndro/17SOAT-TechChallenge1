package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

public class MovimentacaoEstoqueNaoEncontradaException extends RuntimeException {

    public MovimentacaoEstoqueNaoEncontradaException(Integer id) {
        super("Movimentacao de estoque nao encontrada com o id " + id);
    }
}
