package com.kap.mechanics_api.core.movimentacaoestoque.gateway;

import java.util.Optional;

/**
 * Porta mínima para descobrir quem está registrando a movimentação. Fica separada do contexto
 * Usuário para que o estoque dependa só do que precisa: o id correspondente a um login.
 */
public interface ResponsavelMovimentacaoGateway {

    Optional<Integer> buscarIdPorLogin(String login);

}
