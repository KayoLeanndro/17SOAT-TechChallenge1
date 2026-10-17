package com.kap.mechanics_api.core.ordemservico.usecase;

public class OrdemServicoNaoEncontradaException extends RuntimeException {

    public OrdemServicoNaoEncontradaException(Integer id) {
        super("Ordem de servico nao encontrada com o id " + id);
    }
}
