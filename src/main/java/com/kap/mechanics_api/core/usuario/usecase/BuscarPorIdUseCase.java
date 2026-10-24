package com.kap.mechanics_api.core.usuario.usecase;

import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;

public class BuscarPorIdUseCase {
    private final UsuarioGateway usuarioGateway;

    public BuscarPorIdUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public Usuario executar(Integer id) {
    return usuarioGateway.buscarPorId(id)
            .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
}
}
