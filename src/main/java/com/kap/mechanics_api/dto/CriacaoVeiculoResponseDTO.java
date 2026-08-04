package com.kap.mechanics_api.dto;

import jakarta.validation.constraints.NotBlank;

public record CriacaoVeiculoResponseDTO(
        Integer id,
        String placa,
        String marca,
        String modelo,
        int ano
) {
}
