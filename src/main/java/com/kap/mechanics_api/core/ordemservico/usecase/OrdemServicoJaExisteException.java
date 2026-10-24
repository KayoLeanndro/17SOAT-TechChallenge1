package com.kap.mechanics_api.core.ordemservico.usecase;

public class OrdemServicoJaExisteException extends RuntimeException {
    public OrdemServicoJaExisteException(String s) {
        super(s);
    }
}
