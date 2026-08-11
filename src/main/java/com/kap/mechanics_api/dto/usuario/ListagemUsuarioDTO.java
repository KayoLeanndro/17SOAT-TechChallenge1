package com.kap.mechanics_api.dto.usuario;

import com.kap.mechanics_api.enums.TipoUsuario;

import java.time.LocalDateTime;

public record ListagemUsuarioDTO(

        Integer id,
        String nome,
        String senha,
        String login,
        LocalDateTime dataCriacao,
        TipoUsuario tipo
) {
}
