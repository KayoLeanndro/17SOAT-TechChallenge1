package com.kap.mechanics_api.exception;

public class VeiculoNaoEncontradoException extends RuntimeException {
    public VeiculoNaoEncontradoException(Integer id) {
        super("Veiculo nao encontrado com o id " + id);
    }
}
