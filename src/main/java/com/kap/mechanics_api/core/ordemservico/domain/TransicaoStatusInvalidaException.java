package com.kap.mechanics_api.core.ordemservico.domain;

public class TransicaoStatusInvalidaException extends RuntimeException {
    public TransicaoStatusInvalidaException(String s) {
        super(s);
    }
}
