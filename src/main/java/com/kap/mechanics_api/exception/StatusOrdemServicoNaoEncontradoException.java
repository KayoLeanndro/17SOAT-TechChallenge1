package com.kap.mechanics_api.exception;

public class StatusOrdemServicoNaoEncontradoException extends RuntimeException {

    public StatusOrdemServicoNaoEncontradoException(Integer id) {
        super("Status da ordem de serviço não encontrado com o id " + id);
    }
}
