package com.kap.mechanics_api.dto.insumo;

import java.math.BigDecimal;

public record InsumoResponseDTO(Integer id, String nome, String descricao,
                                BigDecimal valorUnitario, Integer quantidadeAtual,
                                Integer quantidadeMinima, boolean ativo) {
}
