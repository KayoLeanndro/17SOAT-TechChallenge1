package com.kap.mechanics_api.core.orcamento.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrcamentoTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 16, 10, 0);

    private Orcamento novo() {
        return Orcamento.novo(1, 2, AGORA);
    }

    @Test
    void deveNascerPendenteComValorZerado() {
        Orcamento orcamento = novo();

        assertNull(orcamento.getId());
        assertEquals(StatusOrcamento.PENDENTE, orcamento.getStatus());
        assertEquals(0, BigDecimal.ZERO.compareTo(orcamento.getValorTotal()));
        assertEquals(AGORA, orcamento.getDataCriacao());
        assertNull(orcamento.getDataResposta());
        assertTrue(orcamento.getItens().isEmpty());
    }

    @Test
    void naoDeveAceitarClienteOuVeiculoNulos() {
        assertThrows(IllegalArgumentException.class, () -> Orcamento.novo(null, 2, AGORA));
        assertThrows(IllegalArgumentException.class, () -> Orcamento.novo(1, null, AGORA));
    }

    @Test
    void orcarServicoDeveSomarMaoDeObraEPecasPadrao() {
        Orcamento orcamento = novo();

        orcamento.orcarServico(10, new BigDecimal("100.00"), List.of(
                new ComponenteOrcado(7, 2, new BigDecimal("30.00")),
                new ComponenteOrcado(8, 1, new BigDecimal("15.00"))));

        assertEquals(0, new BigDecimal("175.00").compareTo(orcamento.getValorTotal()));
        assertEquals(1, orcamento.getServicosOrcados().size());
        assertEquals(0, new BigDecimal("175.00").compareTo(orcamento.getServicosOrcados().getFirst().valorCobrado()));
        // uma linha do serviço + uma por peça
        assertEquals(3, orcamento.getItens().size());
        assertTrue(orcamento.getItens().getFirst().isServico());
        assertEquals(10, orcamento.getItens().getFirst().getServicoId());
        assertEquals(7, orcamento.getItens().get(1).getItemEstoqueId());
    }

    @Test
    void orcarVariosServicosDeveSomarOsValores() {
        Orcamento orcamento = novo();

        orcamento.orcarServico(10, new BigDecimal("100.00"), List.of());
        orcamento.orcarServico(11, new BigDecimal("50.00"), List.of(
                new ComponenteOrcado(7, 3, new BigDecimal("10.00"))));

        assertEquals(0, new BigDecimal("180.00").compareTo(orcamento.getValorTotal()));
        assertEquals(2, orcamento.getServicosOrcados().size());
    }

    @Test
    void incluirServicoDeveMultiplicarMaoDeObraEPecasPelaQuantidade() {
        Orcamento orcamento = novo();

        orcamento.incluirServico(10, new BigDecimal("100.00"), 2, List.of(
                new ComponenteOrcado(7, 3, new BigDecimal("10.00"))));

        // 2 x 100 + (2 x 3) x 10
        assertEquals(0, new BigDecimal("260.00").compareTo(orcamento.getValorTotal()));
        assertEquals(2, orcamento.getItens().get(0).getQuantidade());
        assertEquals(6, orcamento.getItens().get(1).getQuantidade());
        // inclusão avulsa não cria serviço orçado
        assertTrue(orcamento.getServicosOrcados().isEmpty());
    }

    @Test
    void incluirItemEstoqueDeveAcrescentarAoTotal() {
        Orcamento orcamento = novo();

        orcamento.incluirItemEstoque(7, new BigDecimal("25.00"), 4);

        assertEquals(0, new BigDecimal("100.00").compareTo(orcamento.getValorTotal()));
        assertFalse(orcamento.getItens().getFirst().isServico());
    }

    @Test
    void naoDeveIncluirItensEmOrcamentoJaRespondido() {
        Orcamento orcamento = novo();
        orcamento.alterarStatus(StatusOrcamento.APROVADO, AGORA);

        assertThrows(IllegalArgumentException.class,
                () -> orcamento.incluirItemEstoque(7, BigDecimal.TEN, 1));
        assertThrows(IllegalArgumentException.class,
                () -> orcamento.incluirServico(10, BigDecimal.TEN, 1, List.of()));
    }

    @Test
    void alterarStatusDeveRegistrarDataDeRespostaSoParaAprovadoOuRejeitado() {
        Orcamento aprovado = novo();
        aprovado.alterarStatus(StatusOrcamento.APROVADO, AGORA.plusHours(1));
        Orcamento rejeitado = novo();
        rejeitado.alterarStatus(StatusOrcamento.REJEITADO, AGORA.plusHours(2));
        Orcamento pendente = novo();
        pendente.alterarStatus(StatusOrcamento.PENDENTE, AGORA.plusHours(3));

        assertEquals(AGORA.plusHours(1), aprovado.getDataResposta());
        assertEquals(AGORA.plusHours(2), rejeitado.getDataResposta());
        assertNull(pendente.getDataResposta());
    }

    @Test
    void itensExpostosNaoDevemPermitirAlteracaoExterna() {
        Orcamento orcamento = novo();

        assertThrows(UnsupportedOperationException.class,
                () -> orcamento.getItens().add(ItemOrcamento.doEstoque(1, 1, BigDecimal.ONE)));
    }

    @Test
    void converterStatusDeveIgnorarCaixaEEspacos() {
        assertEquals(StatusOrcamento.APROVADO, StatusOrcamento.converter("  aprovado "));
    }

    @Test
    void converterStatusDeveRejeitarValorInvalidoOuVazio() {
        assertThrows(StatusOrcamentoInvalidoException.class, () -> StatusOrcamento.converter("TALVEZ"));
        assertThrows(StatusOrcamentoInvalidoException.class, () -> StatusOrcamento.converter(" "));
        assertThrows(StatusOrcamentoInvalidoException.class, () -> StatusOrcamento.converter(null));
    }
}
