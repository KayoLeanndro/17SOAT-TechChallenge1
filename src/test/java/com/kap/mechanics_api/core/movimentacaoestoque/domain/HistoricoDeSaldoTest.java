package com.kap.mechanics_api.core.movimentacaoestoque.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class HistoricoDeSaldoTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 7, 10, 30);

    private MovimentacaoEstoque entrada(int id, int quantidade) {
        return MovimentacaoEstoque.reconstruir(id, 1, TipoMovimentacaoEstoque.ENTRADA, quantidade, DATA, 2, null);
    }

    private MovimentacaoEstoque saida(int id, int quantidade) {
        return MovimentacaoEstoque.reconstruir(id, 1, TipoMovimentacaoEstoque.SAIDA, quantidade, DATA, 2, 9);
    }

    @Test
    void deveCalcularSaldoAposCadaLancamentoPartindoDoSaldoAtual() {
        // saldo atual 14 = 0 + 10 - 3 + 7 → saldos: 10, 7, 14
        Map<Integer, Integer> saldos = HistoricoDeSaldo.saldoAposCadaMovimentacao(
                14, List.of(entrada(1, 10), saida(2, 3), entrada(3, 7)));

        assertEquals(10, saldos.get(1));
        assertEquals(7, saldos.get(2));
        assertEquals(14, saldos.get(3));
    }

    @Test
    void deveConsiderarSaldoInicialAnteriorAoLivroRazao() {
        // item já tinha 5 antes de qualquer lançamento: 5 + 10 - 4 = 11
        Map<Integer, Integer> saldos = HistoricoDeSaldo.saldoAposCadaMovimentacao(
                11, List.of(entrada(1, 10), saida(2, 4)));

        assertEquals(15, saldos.get(1));
        assertEquals(11, saldos.get(2));
    }

    @Test
    void historicoVazioNaoGeraSaldos() {
        assertTrue(HistoricoDeSaldo.saldoAposCadaMovimentacao(5, List.of()).isEmpty());
    }
}
