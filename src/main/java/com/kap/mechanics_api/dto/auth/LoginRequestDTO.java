package com.kap.mechanics_api.dto.auth;

public record LoginRequestDTO(
        String login,
        String senha
) { }
