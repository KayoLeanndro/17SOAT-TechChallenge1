package com.kap.mechanics_api.core.usuario.usecase;

import java.util.List;

import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;

public class ListarUsuarioUseCase {
    private final UsuarioGateway usuarioGateway;

    public ListarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public List<Usuario> executar() {
        return usuarioGateway.listarTodos();
    }

}
