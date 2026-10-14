package com.kap.mechanics_api.core.veiculo.usecase;

public class VeiculoNaoEncontradoException extends RuntimeException {

    public VeiculoNaoEncontradoException(Integer id) {
        super("Veiculo nao encontrado com o id " + id);
    }
}
