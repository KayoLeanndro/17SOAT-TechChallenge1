package com.kap.mechanics_api.core.movimentacaoestoque.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MovimentacaoEstoqueTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 7, 10, 30);

    @Test
    void entradaDeveNascerSemIdENemOrdemDeServico() {
        MovimentacaoEstoque m = MovimentacaoEstoque.entrada(1, 10, 2, DATA);

        assertNull(m.getId());
        assertEquals(TipoMovimentacaoEstoque.ENTRADA, m.getTipo());
        assertEquals(1, m.getItemEstoqueId());
        assertEquals(10, m.getQuantidade());
        assertEquals(2, m.getUsuarioId());
        assertNull(m.getOrdemServicoId());
        assertEquals(DATA, m.getDataHora());
        assertEquals(10, m.variacaoDeSaldo());
    }

    @Test
    void saidaDeveGuardarAOrdemDeServicoEVariarNegativamente() {
        MovimentacaoEstoque m = MovimentacaoEstoque.saida(1, 4, 2, 99, DATA);

        assertEquals(TipoMovimentacaoEstoque.SAIDA, m.getTipo());
        assertEquals(99, m.getOrdemServicoId());
        assertEquals(-4, m.variacaoDeSaldo());
    }

    @Test
    void saidaDeveExigirOrdemDeServico() {
        assertThrows(IllegalArgumentException.class, () -> MovimentacaoEstoque.saida(1, 4, 2, null, DATA));
    }

    @Test
    void deveRejeitarCamposObrigatoriosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> MovimentacaoEstoque.entrada(null, 1, 2, DATA));
        assertThrows(IllegalArgumentException.class, () -> MovimentacaoEstoque.entrada(1, null, 2, DATA));
        assertThrows(IllegalArgumentException.class, () -> MovimentacaoEstoque.entrada(1, 0, 2, DATA));
        assertThrows(IllegalArgumentException.class, () -> MovimentacaoEstoque.entrada(1, -3, 2, DATA));
        assertThrows(IllegalArgumentException.class, () -> MovimentacaoEstoque.entrada(1, 1, null, DATA));
        assertThrows(IllegalArgumentException.class, () -> MovimentacaoEstoque.entrada(1, 1, 2, null));
        assertThrows(IllegalArgumentException.class,
                () -> MovimentacaoEstoque.reconstruir(1, 1, null, 1, DATA, 2, null));
    }

    @Test
    void reconstruirDevePreservarId() {
        MovimentacaoEstoque m = MovimentacaoEstoque.reconstruir(7, 1, TipoMovimentacaoEstoque.ENTRADA, 5, DATA, 2, null);

        assertEquals(7, m.getId());
    }
}
