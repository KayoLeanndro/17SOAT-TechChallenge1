package com.kap.mechanics_api.core.usuario.usecase;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(Integer id) {
        super("Usuario não encontrado de id: " + id);
    }

    public UsuarioNaoEncontradoException(String login) {
        super("Usuario não encontrado: " + login);
    }
}
