package com.kap.mechanics_api.dto.usuario;

import java.time.LocalDateTime;

public record CriacaoUsuarioResponseDTO(
        Integer id,
        String nome,
        String login,
        LocalDateTime dataCriacao
) {
}
