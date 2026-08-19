package com.kap.mechanics_api.dto.orcamento;

import jakarta.validation.constraints.NotBlank;

public record AtualizacaoStatusOrcamentoRequestDTO(
        @NotBlank
        String status
) {
}
