package com.kap.mechanics_api.core.itemestoque.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ItemEstoqueTest {

    private ItemEstoque filtro(int quantidadeAtual, boolean ativo) {
        return ItemEstoque.reconstruir(1, "Filtro", "Filtro de óleo", TipoItemEstoque.PECA,
                new BigDecimal("25.00"), quantidadeAtual, 2, ativo);
    }

    @Test
    void novoDeveNascerComSaldoZeroESemId() {
        ItemEstoque item = ItemEstoque.novo("Filtro", "Filtro de óleo", TipoItemEstoque.PECA,
                new BigDecimal("25.00"), 2, true);

        assertNull(item.getId());
        assertEquals(0, item.getQuantidadeAtual());
        assertEquals(2, item.getQuantidadeMinima());
        assertEquals(TipoItemEstoque.PECA, item.getTipoItemEstoque());
        assertTrue(item.isAtivo());
    }

    @Test
    void deveRejeitarDadosObrigatoriosInvalidos() {
        BigDecimal v = BigDecimal.ONE;
        assertThrows(IllegalArgumentException.class, () -> ItemEstoque.novo(null, "d", TipoItemEstoque.PECA, v, 0, true));
        assertThrows(IllegalArgumentException.class, () -> ItemEstoque.novo(" ", "d", TipoItemEstoque.PECA, v, 0, true));
        assertThrows(IllegalArgumentException.class, () -> ItemEstoque.novo("n", null, TipoItemEstoque.PECA, v, 0, true));
        assertThrows(IllegalArgumentException.class, () -> ItemEstoque.novo("n", "d", null, v, 0, true));
        assertThrows(IllegalArgumentException.class, () -> ItemEstoque.novo("n", "d", TipoItemEstoque.PECA, null, 0, true));
        assertThrows(IllegalArgumentException.class,
                () -> ItemEstoque.novo("n", "d", TipoItemEstoque.PECA, new BigDecimal("-1"), 0, true));
        assertThrows(IllegalArgumentException.class, () -> ItemEstoque.novo("n", "d", TipoItemEstoque.PECA, v, -1, true));
        assertThrows(IllegalArgumentException.class,
                () -> ItemEstoque.reconstruir(1, "n", "d", TipoItemEstoque.PECA, v, -1, 0, true));
    }

    @Test
    void atualizarDeveAlterarSomenteOsCamposInformadosSemTocarNoSaldo() {
        ItemEstoque item = filtro(10, true);

        item.atualizar("Filtro novo", null, TipoItemEstoque.INSUMO, null, 5, false);

        assertEquals("Filtro novo", item.getNome());
        assertEquals("Filtro de óleo", item.getDescricao());
        assertEquals(TipoItemEstoque.INSUMO, item.getTipoItemEstoque());
        assertEquals(new BigDecimal("25.00"), item.getValorUnitario());
        assertEquals(5, item.getQuantidadeMinima());
        assertFalse(item.isAtivo());
        assertEquals(10, item.getQuantidadeAtual());
    }

    @Test
    void atualizarDeveRejeitarValorEQuantidadeMinimaNegativos() {
        ItemEstoque item = filtro(10, true);

        assertThrows(IllegalArgumentException.class,
                () -> item.atualizar(null, null, null, new BigDecimal("-1"), null, null));
        assertThrows(IllegalArgumentException.class, () -> item.atualizar(null, null, null, null, -1, null));
    }

    @Test
    void registrarEntradaDeveSomarAoSaldo() {
        ItemEstoque item = filtro(10, true);

        item.registrarEntrada(5);

        assertEquals(15, item.getQuantidadeAtual());
    }

    @Test
    void registrarSaidaDeveSubtrairDoSaldoPermitindoZerar() {
        ItemEstoque item = filtro(10, true);

        item.registrarSaida(10);

        assertEquals(0, item.getQuantidadeAtual());
    }

    @Test
    void registrarSaidaDeveFalharQuandoSaldoInsuficienteSemAlterarOSaldo() {
        ItemEstoque item = filtro(3, true);

        EstoqueInsuficienteException ex = assertThrows(EstoqueInsuficienteException.class,
                () -> item.registrarSaida(4));

        assertEquals("Estoque insuficiente para o item 1. Disponivel: 3, solicitado: 4", ex.getMessage());
        assertEquals(3, item.getQuantidadeAtual());
    }

    @Test
    void itemInativoNaoPodeReceberNemDarSaida() {
        ItemEstoque item = filtro(10, false);

        assertThrows(ItemEstoqueInativoException.class, () -> item.registrarEntrada(1));
        assertThrows(ItemEstoqueInativoException.class, () -> item.registrarSaida(1));
        assertEquals(10, item.getQuantidadeAtual());
    }

    @Test
    void movimentacaoDeveExigirQuantidadePositiva() {
        ItemEstoque item = filtro(10, true);

        assertThrows(IllegalArgumentException.class, () -> item.registrarEntrada(0));
        assertThrows(IllegalArgumentException.class, () -> item.registrarSaida(-1));
    }

    @Test
    void entradaDeveFalharEmVezDeEstourarOInteiro() {
        ItemEstoque item = filtro(Integer.MAX_VALUE, true);

        assertThrows(ArithmeticException.class, () -> item.registrarEntrada(1));
    }
}
