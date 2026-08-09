package com.kap.mechanics_api.dto.veiculo;

import org.springframework.util.StringUtils;

public record AtualizacaoVeiculoRequestDTO(
        String placa,
        String marca,
        String modelo,
        Integer ano
) {

    public boolean temAoMenosUmCampoPreenchido() {
        return StringUtils.hasText(placa) || ano != null || StringUtils.hasText(marca)  || StringUtils.hasText(modelo);
    }
}
