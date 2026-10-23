package com.kap.mechanics_api.core.usuario.usecase;

import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;

public class ExcluirUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;

    public ExcluirUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public void executar(Integer id) {
        usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        usuarioGateway.excluir(id);
    }
}