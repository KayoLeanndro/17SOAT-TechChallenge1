package com.kap.mechanics_api.exception;

public class OrcamentoNaoEncontradoException extends RuntimeException {
    public OrcamentoNaoEncontradoException(Integer id) {
        super("Orçamento não encontrado com id: " + id);
    }

    public OrcamentoNaoEncontradoException(String message) {
        super(message);
    }
}
