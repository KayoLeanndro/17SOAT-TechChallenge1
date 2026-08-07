package com.kap.mechanics_api.dto.servico;

public record CriacaoServicoResponseDTO(
        Integer id,
        String nome,
        String descricao,
        String valorMaoDeObra,
        Integer tempoEstimadoMin,
        Boolean ativo
) {
}
