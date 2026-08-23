package com.kap.mechanics_api.exception;

public class OrdemServicoNaoEncontradaException extends RuntimeException {

    public OrdemServicoNaoEncontradaException(Integer id) {
        super("Ordem de servico nao encontrada com o id " + id);
    }
}
