package com.kap.mechanics_api.dto.orcamento;

import com.kap.mechanics_api.enums.StatusOrcamento;
import jakarta.validation.constraints.NotNull;

public record AtualizacaoStatusOrcamentoRequestDTO(
        @NotNull(message = "Informe o status do orçamento")
        StatusOrcamento status
) {
}
