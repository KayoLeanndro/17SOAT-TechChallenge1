package com.kap.mechanics_api.core.orcamento.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ItemOrcamentoTest {

    @Test
    void deveCalcularValorTotalDaLinha() {
        ItemOrcamento item = ItemOrcamento.doEstoque(7, 3, new BigDecimal("12.50"));

        assertEquals(0, new BigDecimal("37.50").compareTo(item.valorTotal()));
    }

    @Test
    void deveExigirExatamenteUmaReferencia() {
        assertThrows(IllegalArgumentException.class,
                () -> ItemOrcamento.reconstruir(1, null, null, 1, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> ItemOrcamento.reconstruir(1, 2, 3, 1, BigDecimal.ONE));
    }

    @Test
    void deveRejeitarQuantidadeEValorInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> ItemOrcamento.doServico(1, 0, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> ItemOrcamento.doServico(1, 1, new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class, () -> ItemOrcamento.doServico(1, 1, null));
    }
}
