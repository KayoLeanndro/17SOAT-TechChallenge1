package com.kap.mechanics_api.dto.usuario;

import com.kap.mechanics_api.enums.TipoUsuario;

import java.time.LocalDateTime;

public record AtualizacaoUsuarioResponseDTO(
        Integer id,
        String nome,
        String login,
        String senha,
        TipoUsuario tipo
) {
}
