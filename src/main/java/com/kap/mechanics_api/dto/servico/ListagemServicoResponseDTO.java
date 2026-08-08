package com.kap.mechanics_api.dto.servico;

import java.math.BigDecimal;

public record ListagemServicoResponseDTO(
        String nome,
        String descricao,
        BigDecimal valorMaoDeObra,
        Integer tempoEstimadoMin,
        Boolean ativo
) {
}
