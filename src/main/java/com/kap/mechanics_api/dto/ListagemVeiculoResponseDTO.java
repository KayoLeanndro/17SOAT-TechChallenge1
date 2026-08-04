package com.kap.mechanics_api.dto;

public record ListagemVeiculoResponseDTO(
        Integer id,
        String placa,
        String marca,
        String modelo,
        int ano
) {
}
