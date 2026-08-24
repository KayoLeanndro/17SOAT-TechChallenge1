package com.kap.mechanics_api.exception;

public class MovimentacaoEstoqueObrigatoriaException extends RuntimeException {

    public MovimentacaoEstoqueObrigatoriaException() {
        super("A quantidade atual deve ser alterada somente por movimentacoes de estoque.");
    }
}
