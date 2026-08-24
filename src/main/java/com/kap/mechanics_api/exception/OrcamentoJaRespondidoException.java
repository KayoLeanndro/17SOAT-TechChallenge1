package com.kap.mechanics_api.exception;

public class OrcamentoJaRespondidoException extends RuntimeException{
    public OrcamentoJaRespondidoException(Integer id){
        super("id: " + id);
    }
}
