package com.kap.mechanics_api.dto.servico;

public record AtualizacaoServicoResponseDTO(
        String nome,
        String descricao,
        String valorMaoDeObra,
        Integer tempoEstimadoMin,
        Boolean ativo
) {
}
