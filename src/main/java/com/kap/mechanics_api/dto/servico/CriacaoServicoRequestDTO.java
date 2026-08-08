package com.kap.mechanics_api.dto.servico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
public record CriacaoServicoRequestDTO(

        @Pattern(
                regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
                message = "Placa inválida."
        )
        String placa,

        @NotBlank(message = "informa a marca do veículo")
        String marca,

        @NotBlank(message = "informe o modelo do veículo")
        String modelo,

        @NotNull
        Integer ano
) {
}
