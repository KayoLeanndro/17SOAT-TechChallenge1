package com.kap.mechanics_api.core.servico.gateway;

/**
 * Peça padrão de um serviço (o que o serviço consome do estoque por execução).
 */
public record ComponenteDeServico(Integer itemEstoqueId, int quantidadePadrao) {
}
