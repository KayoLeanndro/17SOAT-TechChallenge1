package com.kap.mechanics_api.core.orcamento.domain;

import java.math.BigDecimal;

/**
 * Serviço contratado no orçamento, com o valor cobrado (mão de obra + peças padrão).
 */
public record ServicoOrcado(Integer servicoId, BigDecimal valorCobrado) {
}
