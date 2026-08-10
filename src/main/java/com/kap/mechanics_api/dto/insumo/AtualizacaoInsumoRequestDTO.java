package com.kap.mechanics_api.dto.insumo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public record AtualizacaoInsumoRequestDTO(
        @Pattern(regexp = ".*\\S.*", message = "O nome não pode estar em branco") String nome,
        @Pattern(regexp = ".*\\S.*", message = "A descrição não pode estar em branco") String descricao,
        @DecimalMin(value = "0.0", message = "O valor unitário não pode ser negativo") BigDecimal valorUnitario,
        @PositiveOrZero(message = "A quantidade atual não pode ser negativa") Integer quantidadeAtual,
        @PositiveOrZero(message = "A quantidade mínima não pode ser negativa") Integer quantidadeMinima,
        Boolean ativo
) {
    public boolean temAoMenosUmCampoPreenchido() {
        return StringUtils.hasText(nome) || StringUtils.hasText(descricao)
                || valorUnitario != null || quantidadeAtual != null
                || quantidadeMinima != null || ativo != null;
    }
}
