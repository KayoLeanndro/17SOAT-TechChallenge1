package com.kap.mechanics_api.exception;

import java.time.LocalDateTime;

public class PeriodoMovimentacaoInvalidoException extends RuntimeException {

    public PeriodoMovimentacaoInvalidoException(LocalDateTime inicio, LocalDateTime fim) {
        super("Periodo invalido: inicio " + inicio + " deve ser anterior ou igual a fim " + fim);
    }
}
