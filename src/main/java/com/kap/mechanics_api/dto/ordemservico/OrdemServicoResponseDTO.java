package com.kap.mechanics_api.dto.ordemservico;

import java.time.LocalDateTime;

public record OrdemServicoResponseDTO(
        Integer id,
        Integer orcamentoId,
        Integer usuarioAtendenteId,
        Integer statusId,
        String status,
        LocalDateTime dataAbertura,
        LocalDateTime dataEntrega
) {
}
