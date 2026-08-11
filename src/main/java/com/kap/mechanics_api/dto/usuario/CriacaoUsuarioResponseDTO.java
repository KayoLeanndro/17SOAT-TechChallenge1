package com.kap.mechanics_api.dto.usuario;

import com.kap.mechanics_api.enums.TipoUsuario;

import java.time.LocalDateTime;

public record CriacaoUsuarioResponseDTO(
        Integer id,
        String nome,
        String login,
        LocalDateTime dataCriacao,
        TipoUsuario tipo
) {
}
