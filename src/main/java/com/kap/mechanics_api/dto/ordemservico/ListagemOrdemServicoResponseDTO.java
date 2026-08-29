package com.kap.mechanics_api.dto.ordemservico;

import java.time.LocalDateTime;

public record ListagemOrdemServicoResponseDTO(
        Integer id,
        Integer orcamentoId,
        String status,
        LocalDateTime dataAbertura,
        LocalDateTime dataEntrega
) {
}
