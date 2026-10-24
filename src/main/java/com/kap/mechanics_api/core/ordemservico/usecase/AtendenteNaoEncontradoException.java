package com.kap.mechanics_api.core.ordemservico.usecase;

public class AtendenteNaoEncontradoException extends RuntimeException {

    public AtendenteNaoEncontradoException(String login) {
        super("Usuario não encontrado: " + login);
    }
}
