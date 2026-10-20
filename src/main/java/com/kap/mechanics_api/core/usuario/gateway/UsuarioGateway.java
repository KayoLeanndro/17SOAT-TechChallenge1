package com.kap.mechanics_api.core.usuario.gateway;

import com.kap.mechanics_api.core.usuario.domain.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioGateway {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(Integer id);

    List<Usuario> listarTodos();

    void excluir(Integer id);
}