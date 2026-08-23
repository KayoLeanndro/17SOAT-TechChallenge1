package com.kap.mechanics_api.exception;

public class OrdemServicoNaoEstaEmExecucaoException extends RuntimeException {

    public OrdemServicoNaoEstaEmExecucaoException(Long ordemServicoId) {
        super("A ordem de servico " + ordemServicoId + " nao esta em execucao.");
    }
}
