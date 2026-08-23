package com.kap.mechanics_api.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(Integer itemEstoqueId, Integer disponivel, Integer solicitado) {
        super("Estoque insuficiente para o item " + itemEstoqueId
                + ". Disponivel: " + disponivel + ", solicitado: " + solicitado);
    }
}
