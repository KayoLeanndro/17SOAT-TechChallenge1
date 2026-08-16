package com.kap.mechanics_api.exception;

public class OrdemServicoNaoEncontradaException extends RuntimeException {

    public OrdemServicoNaoEncontradaException(Integer id) {
        super("Ordem de serviço não encontrada com o id " + id);
    }
}
