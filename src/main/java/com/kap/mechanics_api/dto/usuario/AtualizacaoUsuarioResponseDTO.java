package com.kap.mechanics_api.dto.usuario;

import java.time.LocalDateTime;

public record AtualizacaoUsuarioResponseDTO(
        Integer id,
        String nome,
        String senha,
        String tipo,
        LocalDateTime dataCriacao
) {
}
