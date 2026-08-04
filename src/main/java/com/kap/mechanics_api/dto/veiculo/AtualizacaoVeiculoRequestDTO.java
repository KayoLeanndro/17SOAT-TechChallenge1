package com.kap.mechanics_api.dto.veiculo;

public record AtualizacaoVeiculoRequestDTO(
        String placa,
        Integer ano,
        String marca
) {

    public boolean temAoMenosUmCampoPreenchido() {
        return placa != null || ano != null || marca != null;
    }
}
