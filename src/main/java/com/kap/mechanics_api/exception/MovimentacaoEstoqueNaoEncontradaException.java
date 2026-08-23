package com.kap.mechanics_api.exception;

public class MovimentacaoEstoqueNaoEncontradaException extends RuntimeException {

    public MovimentacaoEstoqueNaoEncontradaException(Long id) {
        super("Movimentacao de estoque nao encontrada com o id " + id);
    }
}
