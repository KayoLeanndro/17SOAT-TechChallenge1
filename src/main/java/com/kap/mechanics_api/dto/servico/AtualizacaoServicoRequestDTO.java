package com.kap.mechanics_api.dto.servico;

public record AtualizacaoServicoRequestDTO(
        String nome,
        String descricao,
        String valorMaoDeObra,
        Integer tempoEstimadoMin,
        Boolean ativo
) {

    public boolean temAoMenosUmCampoPreenchido() {
        return nome != null || descricao != null || valorMaoDeObra != null
                || tempoEstimadoMin != null || ativo != null ;
    }
}
