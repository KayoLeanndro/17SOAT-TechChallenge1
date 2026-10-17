package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

public class ResponsavelNaoEncontradoException extends RuntimeException {

    public ResponsavelNaoEncontradoException(String login) {
        super("Usuario não encontrado: " + login);
    }
}
