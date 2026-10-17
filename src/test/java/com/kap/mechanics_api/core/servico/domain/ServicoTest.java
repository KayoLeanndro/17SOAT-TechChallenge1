package com.kap.mechanics_api.core.servico.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ServicoTest {

    private Servico alinhamento() {
        return Servico.reconstruir(1, "Alinhamento", "Alinhamento 3D", new BigDecimal("100.00"), 60, true);
    }

    @Test
    void novoDeveCriarServicoSemIdMantendoOsDados() {
        Servico servico = Servico.novo("Alinhamento", "Alinhamento 3D", new BigDecimal("100.00"), 60, true);

        assertNull(servico.getId());
        assertEquals("Alinhamento", servico.getNome());
        assertEquals("Alinhamento 3D", servico.getDescricao());
        assertEquals(new BigDecimal("100.00"), servico.getValorMaoDeObra());
        assertEquals(60, servico.getTempoEstimadoMin());
        assertTrue(servico.isAtivo());
    }

    @Test
    void reconstruirDevePreservarId() {
        assertEquals(1, alinhamento().getId());
    }

    @Test
    void devePermitirValorEZeroETempoZero() {
        Servico servico = Servico.novo("Cortesia", "Sem custo", BigDecimal.ZERO, 0, false);

        assertEquals(0, servico.getValorMaoDeObra().signum());
        assertEquals(0, servico.getTempoEstimadoMin());
        assertFalse(servico.isAtivo());
    }

    @Test
    void deveRejeitarNomeEDescricaoEmBrancoOuNulos() {
        assertThrows(IllegalArgumentException.class, () -> Servico.novo(null, "d", BigDecimal.ONE, 1, true));
        assertThrows(IllegalArgumentException.class, () -> Servico.novo(" ", "d", BigDecimal.ONE, 1, true));
        assertThrows(IllegalArgumentException.class, () -> Servico.novo("n", null, BigDecimal.ONE, 1, true));
        assertThrows(IllegalArgumentException.class, () -> Servico.novo("n", "", BigDecimal.ONE, 1, true));
    }

    @Test
    void deveRejeitarValorENulosOuNegativos() {
        assertThrows(IllegalArgumentException.class, () -> Servico.novo("n", "d", null, 1, true));
        assertThrows(IllegalArgumentException.class, () -> Servico.novo("n", "d", new BigDecimal("-0.01"), 1, true));
        assertThrows(IllegalArgumentException.class, () -> Servico.novo("n", "d", BigDecimal.ONE, null, true));
        assertThrows(IllegalArgumentException.class, () -> Servico.novo("n", "d", BigDecimal.ONE, -1, true));
    }

    @Test
    void atualizarDeveAlterarSomenteOsCamposInformados() {
        Servico servico = alinhamento();

        servico.atualizar(null, "Nova descrição", null, 90, false);

        assertEquals("Alinhamento", servico.getNome());
        assertEquals("Nova descrição", servico.getDescricao());
        assertEquals(new BigDecimal("100.00"), servico.getValorMaoDeObra());
        assertEquals(90, servico.getTempoEstimadoMin());
        assertFalse(servico.isAtivo());
    }

    @Test
    void atualizarDeveIgnorarTextosEmBranco() {
        Servico servico = alinhamento();

        servico.atualizar("  ", "", null, null, null);

        assertEquals("Alinhamento", servico.getNome());
        assertEquals("Alinhamento 3D", servico.getDescricao());
    }

    @Test
    void atualizarDeveRejeitarValoresNegativos() {
        Servico servico = alinhamento();

        assertThrows(IllegalArgumentException.class,
                () -> servico.atualizar(null, null, new BigDecimal("-1"), null, null));
        assertThrows(IllegalArgumentException.class, () -> servico.atualizar(null, null, null, -5, null));
        assertEquals(new BigDecimal("100.00"), servico.getValorMaoDeObra());
        assertEquals(60, servico.getTempoEstimadoMin());
    }
}
