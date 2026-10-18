package com.kap.mechanics_api.core.orcamento.domain;

import java.util.Locale;

public enum StatusOrcamento {
    PENDENTE,
    APROVADO,
    REJEITADO;

    public static StatusOrcamento converter(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new StatusOrcamentoInvalidoException(texto);
        }
        try {
            return valueOf(texto.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new StatusOrcamentoInvalidoException(texto);
        }
    }
}
