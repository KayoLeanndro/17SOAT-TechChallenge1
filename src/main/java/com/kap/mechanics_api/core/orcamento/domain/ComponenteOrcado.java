package com.kap.mechanics_api.core.orcamento.domain;

import java.math.BigDecimal;

/**
 * Peça que acompanha um serviço, já com a quantidade e o preço que serão cobrados.
 */
public record ComponenteOrcado(Integer itemEstoqueId, int quantidade, BigDecimal valorUnitario) {
}
