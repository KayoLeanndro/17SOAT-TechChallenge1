package com.kap.mechanics_api.dto.ordemservico;

import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import jakarta.validation.constraints.NotNull;

public record AtualizacaoStatusOrdemServicoRequestDTO(
        @NotNull(message = "Informe o novo status da ordem de servico")
        StatusOrdemServicoEnum status
) {
}
