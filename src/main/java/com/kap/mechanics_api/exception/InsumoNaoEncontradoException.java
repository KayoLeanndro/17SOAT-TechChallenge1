package com.kap.mechanics_api.exception;

public class InsumoNaoEncontradoException extends RuntimeException {
    public InsumoNaoEncontradoException(Integer id) {
        super("Insumo não encontrado com o id " + id);
    }
}
