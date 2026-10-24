package com.kap.mechanics_api.core.ordemservico.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrdemServicoTest {

    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 10, 16, 9, 0);
    private static final LocalDateTime DEPOIS = ABERTURA.plusHours(2);

    private OrdemServico aberta() {
        return OrdemServico.abrir(5, 3, ABERTURA);
    }

    // ordem já gravada no status informado, com o período aberto correspondente
    private OrdemServico noStatus(StatusOrdemServico status, List<ItemOrdemServico> itens) {
        return OrdemServico.reconstruir(9, 5, 3, status, ABERTURA, null,
                List.of(HistoricoDeStatus.reconstruir(1L, status, ABERTURA, null)), itens);
    }

    @Test
    void deveNascerAguardandoAprovacaoComHistoricoAberto() {
        OrdemServico os = aberta();

        assertNull(os.getId());
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, os.getStatus());
        assertEquals(ABERTURA, os.getDataAbertura());
        assertEquals(1, os.getHistorico().size());
        assertNull(os.getHistorico().getFirst().getFim());
    }

    @Test
    void naoDeveAbrirSemOrcamentoOuAtendente() {
        assertThrows(IllegalArgumentException.class, () -> OrdemServico.abrir(null, 3, ABERTURA));
        assertThrows(IllegalArgumentException.class, () -> OrdemServico.abrir(5, null, ABERTURA));
    }

    @Test
    void transicaoValidaDeveEncerrarPeriodoAnteriorEAbrirOProximo() {
        OrdemServico os = aberta();

        os.transicionarPara(StatusOrdemServico.EM_DIAGNOSTICO, DEPOIS);

        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, os.getStatus());
        assertEquals(2, os.getHistorico().size());
        assertEquals(DEPOIS, os.getHistorico().get(0).getFim());
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, os.getHistorico().get(1).getStatus());
        assertEquals(DEPOIS, os.getHistorico().get(1).getInicio());
        assertNull(os.getHistorico().get(1).getFim());
    }

    @Test
    void transicaoInvalidaNaoDeveAlterarNada() {
        OrdemServico os = aberta();

        TransicaoStatusInvalidaException ex = assertThrows(TransicaoStatusInvalidaException.class,
                () -> os.transicionarPara(StatusOrdemServico.EM_EXECUCAO, DEPOIS));

        assertTrue(ex.getMessage().contains("AGUARDANDO_APROVACAO"));
        assertTrue(ex.getMessage().contains("EM_EXECUCAO"));
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, os.getStatus());
        assertEquals(1, os.getHistorico().size());
        assertNull(os.getHistorico().getFirst().getFim());
    }

    @Test
    void transicaoSemHistoricoAbertoDeveFalhar() {
        OrdemServico semHistorico = OrdemServico.reconstruir(9, 5, 3, StatusOrdemServico.RECEBIDA, ABERTURA,
                null, List.of(), List.of());

        assertThrows(IllegalStateException.class,
                () -> semHistorico.transicionarPara(StatusOrdemServico.EM_DIAGNOSTICO, DEPOIS));
    }

    @Test
    void finalizarPorOrcamentoDeveEncerrarSemValidarOFluxo() {
        OrdemServico os = aberta();

        os.finalizarPorOrcamento(DEPOIS);

        assertEquals(StatusOrdemServico.FINALIZADA, os.getStatus());
        assertEquals(DEPOIS, os.getHistorico().get(0).getFim());
        assertEquals(StatusOrdemServico.FINALIZADA, os.getHistorico().get(1).getStatus());
    }

    @Test
    void deveCopiarItensDoOrcamentoSomenteQuandoAindaNaoTemItens() {
        ItemOrdemServico doOrcamento = ItemOrdemServico.novo(1, null, 7, 2, BigDecimal.TEN);
        OrdemServico vazia = noStatus(StatusOrdemServico.EM_DIAGNOSTICO, List.of());
        OrdemServico comItens = noStatus(StatusOrdemServico.EM_DIAGNOSTICO,
                List.of(ItemOrdemServico.reconstruir(4, null, null, 8, 1, BigDecimal.ONE)));

        vazia.copiarItensDoOrcamento(List.of(doOrcamento));
        comItens.copiarItensDoOrcamento(List.of(doOrcamento));

        assertEquals(1, vazia.getItens().size());
        assertEquals(1, comItens.getItens().size());
        assertEquals(8, comItens.getItens().getFirst().getItemEstoqueId());
    }

    @Test
    void deveIncluirServicoEItemDeEstoque() {
        OrdemServico os = noStatus(StatusOrdemServico.EM_EXECUCAO, List.of());

        os.incluirServico(10, 2, new BigDecimal("80.00"));
        os.incluirItemEstoque(7, 3, new BigDecimal("5.00"));

        assertEquals(2, os.getItens().size());
        assertEquals(10, os.getItens().get(0).getServicoId());
        assertEquals(0, new BigDecimal("160.00").compareTo(os.getItens().get(0).valorTotal()));
        assertEquals(7, os.getItens().get(1).getItemEstoqueId());
    }

    @Test
    void exigirEmExecucaoDeveFalharEmOutrosStatus() {
        assertDoesNotThrow(() -> noStatus(StatusOrdemServico.EM_EXECUCAO, List.of()).exigirEmExecucao());
        assertThrows(OrdemServicoNaoEstaEmExecucaoException.class,
                () -> noStatus(StatusOrdemServico.EM_DIAGNOSTICO, List.of()).exigirEmExecucao());
    }
}
