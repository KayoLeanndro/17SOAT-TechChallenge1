package com.kap.mechanics_api.core.usuario.usecase;

import com.kap.mechanics_api.core.usuario.domain.TipoUsuario;
import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.CriptografiaSenhaGateway;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;

public class CadastrarUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final CriptografiaSenhaGateway criptografiaSenhaGateway;

    public CadastrarUsuarioUseCase(
            UsuarioGateway usuarioGateway,
            CriptografiaSenhaGateway criptografiaSenhaGateway) {
        this.usuarioGateway = usuarioGateway;
        this.criptografiaSenhaGateway = criptografiaSenhaGateway;
    }

    public Usuario executar(
            String nome,
            String login,
            String senha,
            TipoUsuario tipo) {

        String senhaHash = criptografiaSenhaGateway.codificar(senha);

        Usuario usuario = new Usuario(
                null,
                nome,
                login,
                senhaHash,
                tipo
        );

        return usuarioGateway.salvar(usuario);
    }
}