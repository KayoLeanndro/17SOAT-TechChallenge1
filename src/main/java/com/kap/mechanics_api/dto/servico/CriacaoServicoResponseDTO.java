package com.kap.mechanics_api.dto.servico;

import java.math.BigDecimal;

public record CriacaoServicoResponseDTO(
        Integer id,
        String nome,
        String descricao,
        BigDecimal valorMaoDeObra,
        Integer tempoEstimadoMin,
        Boolean ativo
) {
}
