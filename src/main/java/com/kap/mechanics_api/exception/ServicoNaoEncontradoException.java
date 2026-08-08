package com.kap.mechanics_api.exception;

public class ServicoNaoEncontradoException extends RuntimeException {
    public ServicoNaoEncontradoException(Integer id) {
        super("Servico nao encontrado com o id " + id);
    }
}
