package com.kap.mechanics_api.exception;

public class OrdemServicoNaoEncontradaException extends RuntimeException {

    public OrdemServicoNaoEncontradaException(Long id) {
        super("Ordem de servico nao encontrada com o id " + id);
    }
}
