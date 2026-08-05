package com.kap.mechanics_api.dto.veiculo;

public record AtualizacaoVeiculoRequestDTO(
        String placa,
        String marca,
        String modelo,
        Integer ano
) {

    public boolean temAoMenosUmCampoPreenchido() {
        return placa != null || ano != null || marca != null || modelo != null;
    }
}
