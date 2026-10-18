package com.kap.mechanics_api.core.orcamento.domain;

public class StatusOrcamentoInvalidoException extends RuntimeException {
    public StatusOrcamentoInvalidoException(String status) {
        super("Status de orçamento inválido: " + status);
    }
}
