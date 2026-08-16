package com.kap.mechanics_api.dto.ordemservico;

import jakarta.validation.constraints.NotNull;

public record CriacaoOrdemServicoRequestDTO(
        @NotNull(message = "Informe o orçamento da ordem de serviço")
        Integer orcamentoId,
        @NotNull(message = "Informe o atendente responsável")
        Integer usuarioAtendenteId,
        @NotNull(message = "Informe o status da ordem de serviço")
        Integer statusId
) {
}
