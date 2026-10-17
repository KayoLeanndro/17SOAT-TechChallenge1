package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;

@ExtendWith(MockitoExtension.class)
class ConsultasDeMovimentacaoUseCaseTest {

    private static final LocalDateTime T1 = LocalDateTime.of(2026, 10, 7, 9, 0);
    private static final LocalDateTime T2 = LocalDateTime.of(2026, 10, 7, 10, 0);

    @Mock
    private MovimentacaoEstoqueGateway gateway;
    @Mock
    private ItemEstoqueGateway itemGateway;

    private MontadorDeConsultaDeMovimentacao montador;

    // Item com saldo atual 7 = entrada de 10 (id 1) seguida de saída de 3 (id 2)
    private final MovimentacaoEstoque entrada = MovimentacaoEstoque.reconstruir(
            1, 5, TipoMovimentacaoEstoque.ENTRADA, 10, T1, 2, null);
    private final MovimentacaoEstoque saida = MovimentacaoEstoque.reconstruir(
            2, 5, TipoMovimentacaoEstoque.SAIDA, 3, T2, 2, 99);

    @BeforeEach
    void setUp() {
        montador = new MontadorDeConsultaDeMovimentacao(gateway, itemGateway);
    }

    private void itemComHistorico() {
        when(itemGateway.pesquisarPorId(5)).thenReturn(Optional.of(ItemEstoque.reconstruir(
                5, "Filtro", "Óleo", TipoItemEstoque.PECA, BigDecimal.TEN, 7, 1, true)));
        when(gateway.listarHistoricoDoItem(5)).thenReturn(List.of(entrada, saida));
    }

    @Test
    void listarDeveMontarNomeESaldoHistoricoMantendoAOrdemRecebida() {
        itemComHistorico();
        when(gateway.listarTodas()).thenReturn(List.of(saida, entrada));

        List<MovimentacaoEstoqueConsulta> resultado = new ListarMovimentacoesUseCase(gateway, montador).executar();

        assertEquals(2, resultado.size());
        assertEquals(2, resultado.get(0).movimentacao().getId());
        assertEquals(7, resultado.get(0).saldoItemEstoque());
        assertEquals("Filtro", resultado.get(0).itemEstoqueNome());
        assertEquals(1, resultado.get(1).movimentacao().getId());
        assertEquals(10, resultado.get(1).saldoItemEstoque());
    }

    @Test
    void listarSemLancamentosNaoConsultaItens() {
        when(gateway.listarTodas()).thenReturn(List.of());

        assertTrue(new ListarMovimentacoesUseCase(gateway, montador).executar().isEmpty());

        verify(itemGateway, never()).pesquisarPorId(any());
    }

    @Test
    void listarPorItemOrdemTipoEPeriodoDevemUsarAsConsultasDoGateway() {
        itemComHistorico();
        when(gateway.listarPorItem(5)).thenReturn(List.of(entrada));
        when(gateway.listarPorOrdemServico(99)).thenReturn(List.of(saida));
        when(gateway.listarPorTipo(TipoMovimentacaoEstoque.ENTRADA)).thenReturn(List.of(entrada));
        when(gateway.listarPorPeriodo(T1, T2)).thenReturn(List.of(entrada, saida));

        assertEquals(10, new ListarMovimentacoesPorItemUseCase(gateway, montador).executar(5)
                .get(0).saldoItemEstoque());
        assertEquals(7, new ListarMovimentacoesPorOrdemServicoUseCase(gateway, montador).executar(99)
                .get(0).saldoItemEstoque());
        assertEquals(1, new ListarMovimentacoesPorTipoUseCase(gateway, montador)
                .executar(TipoMovimentacaoEstoque.ENTRADA).size());
        assertEquals(2, new ListarMovimentacoesPorPeriodoUseCase(gateway, montador).executar(T1, T2).size());
    }

    @Test
    void periodoInvertidoDeveLancarExcecaoSemConsultarOGateway() {
        assertThrows(PeriodoMovimentacaoInvalidoException.class,
                () -> new ListarMovimentacoesPorPeriodoUseCase(gateway, montador).executar(T2, T1));

        verify(gateway, never()).listarPorPeriodo(any(), any());
    }

    @Test
    void buscarPorIdDeveDevolverOSaldoDaquelePontoDoHistorico() {
        itemComHistorico();
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(entrada));

        MovimentacaoEstoqueConsulta consulta = new BuscarMovimentacaoPorIdUseCase(gateway, montador).executar(1);

        assertEquals(10, consulta.saldoItemEstoque());
        assertEquals("Filtro", consulta.itemEstoqueNome());
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoExiste() {
        when(gateway.pesquisarPorId(9)).thenReturn(Optional.empty());

        assertThrows(MovimentacaoEstoqueNaoEncontradaException.class,
                () -> new BuscarMovimentacaoPorIdUseCase(gateway, montador).executar(9));
    }

    @Test
    void deveLancarExcecaoQuandoOItemDoLancamentoNaoExiste() {
        when(itemGateway.pesquisarPorId(5)).thenReturn(Optional.empty());
        when(gateway.listarTodas()).thenReturn(List.of(entrada));

        assertThrows(ItemEstoqueNaoEncontradoException.class,
                () -> new ListarMovimentacoesUseCase(gateway, montador).executar());
    }
}
