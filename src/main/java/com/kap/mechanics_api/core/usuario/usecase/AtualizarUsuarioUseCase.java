package com.kap.mechanics_api.core.usuario.usecase;

import com.kap.mechanics_api.core.usuario.domain.TipoUsuario;
import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.CriptografiaSenhaGateway;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;

public class AtualizarUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final CriptografiaSenhaGateway criptografiaSenhaGateway;

    public AtualizarUsuarioUseCase(
            UsuarioGateway usuarioGateway,
            CriptografiaSenhaGateway criptografiaSenhaGateway) {
        this.usuarioGateway = usuarioGateway;
        this.criptografiaSenhaGateway = criptografiaSenhaGateway;
    }

    public Usuario executar(
            Integer id,
            String nome,
            String login,
            String senha,
            TipoUsuario tipo) {

        Usuario usuario = usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
        }

        if (login != null && !login.isBlank()) {
            usuario.setLogin(login);
        }

        if (senha != null && !senha.isBlank()) {
            usuario.setSenhaHash(criptografiaSenhaGateway.codificar(senha));
        }

        if (tipo != null) {
            usuario.setTipo(tipo);
        }

        return usuarioGateway.salvar(usuario);
    }
}