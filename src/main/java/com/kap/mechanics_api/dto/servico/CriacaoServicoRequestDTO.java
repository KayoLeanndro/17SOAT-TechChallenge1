package com.kap.mechanics_api.dto.servico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CriacaoServicoRequestDTO(

        @NotBlank(message = "Informe o nome do serviço")
        String nome,

        @NotBlank(message = "Informe a descrição do serviço")
        String descricao,

        @NotNull(message = "Informe o valor da mão de obra")
        @PositiveOrZero(message = "O valor da mão de obra não pode ser negativo")
        BigDecimal valorMaoDeObra,

        @NotNull(message = "Informe o tempo estimado")
        @PositiveOrZero(message = "O tempo estimado não pode ser negativo")
        Integer tempoEstimadoMin,

        @NotNull(message = "Informe se o serviço está ativo")
        Boolean ativo
) {
}
