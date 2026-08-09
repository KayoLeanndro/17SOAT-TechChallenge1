package com.kap.mechanics_api.exception;

public class PecaNaoEncontradaException extends RuntimeException {

    public PecaNaoEncontradaException(Integer id) {
        super("Peça ou insumo não encontrado com o id " + id);
    }
}
