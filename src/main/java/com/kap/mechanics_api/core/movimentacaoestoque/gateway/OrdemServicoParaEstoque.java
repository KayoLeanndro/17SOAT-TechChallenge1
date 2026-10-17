package com.kap.mechanics_api.core.movimentacaoestoque.gateway;

/**
 * Visão da ordem de serviço com o que o estoque precisa saber dela.
 */
public record OrdemServicoParaEstoque(Integer id, boolean emExecucao, Integer usuarioAtendenteId) {
}
