package com.kap.mechanics_api.dto.itemestoque;

import com.kap.mechanics_api.enums.TipoItemEstoque;

import java.math.BigDecimal;

public record ItemEstoqueResponseDTO(
        Integer id,
        String nome,
        String descricao,
        TipoItemEstoque tipoItemEstoque,
        BigDecimal valorUnitario,
        Integer quantidadeAtual,
        Integer quantidadeMinima,
        boolean ativo
) {
}
