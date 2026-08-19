package com.kap.mechanics_api.dto.orcamento;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GeracaoOrcamentoRequestDTO(
        @NotNull
        Integer clienteId,

        @NotNull
        Integer veiculoId,

        @NotNull
        List<Integer> servicosIds
) {
}
