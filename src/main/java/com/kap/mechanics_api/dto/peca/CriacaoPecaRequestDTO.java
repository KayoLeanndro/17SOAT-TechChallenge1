package com.kap.mechanics_api.dto.peca;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CriacaoPecaRequestDTO(
        @NotBlank(message = "Informe o nome da peça ou insumo") String nome,
        @NotBlank(message = "Informe a descrição da peça ou insumo") String descricao,
        @NotNull @DecimalMin(value = "0.0", inclusive = true, message = "O valor unitário não pode ser negativo") BigDecimal valorUnitario,
        @NotNull @PositiveOrZero(message = "A quantidade atual não pode ser negativa") Integer quantidadeAtual,
        @NotNull @PositiveOrZero(message = "A quantidade mínima não pode ser negativa") Integer quantidadeMinima,
        @NotNull Boolean ativo
) {
}
