package com.kap.mechanics_api.dto.peca;

import java.math.BigDecimal;

public record PecaResponseDTO(
        Integer id,
        String nome,
        String descricao,
        BigDecimal valorUnitario,
        Integer quantidadeAtual,
        Integer quantidadeMinima,
        boolean ativo
) {
}
