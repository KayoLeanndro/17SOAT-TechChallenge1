package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.EstoqueInsuficienteException;
import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoParaEstoque;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEstaEmExecucaoException;

@ExtendWith(MockitoExtension.class)
class BaixarItemParaOrdemServicoUseCaseTest {

    @Mock
    private ItemEstoqueGateway itemGateway;
    @Mock
    private MovimentacaoEstoqueGateway movimentacaoGateway;
    @Mock
    private OrdemServicoEstoqueGateway ordemServicoGateway;

    private BaixarItemParaOrdemServicoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BaixarItemParaOrdemServicoUseCase(itemGateway, movimentacaoGateway, ordemServicoGateway,
                Clock.fixed(Instant.parse("2026-10-07T13:30:00Z"), ZoneOffset.UTC));
    }

    private ItemEstoque item(int saldo) {
        return ItemEstoque.reconstruir(5, "Filtro", "Óleo", TipoItemEstoque.PECA, BigDecimal.TEN, saldo, 1, true);
    }

    @Test
    void deveBaixarEmNomeDoAtendenteDaOrdem() {
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.of(new OrdemServicoParaEstoque(99, true, 3)));
        ItemEstoque item = item(10);
        when(itemGateway.pesquisarPorIdParaAtualizacao(5)).thenReturn(Optional.of(item));

        useCase.executar(99, 5, 4);

        assertEquals(6, item.getQuantidadeAtual());
        verify(itemGateway).salvar(item);
        ArgumentCaptor<MovimentacaoEstoque> captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movimentacaoGateway).salvar(captor.capture());
        assertEquals(TipoMovimentacaoEstoque.SAIDA, captor.getValue().getTipo());
        assertEquals(3, captor.getValue().getUsuarioId());
        assertEquals(99, captor.getValue().getOrdemServicoId());
        assertEquals(4, captor.getValue().getQuantidade());
    }

    @Test
    void deveLancarExcecaoQuandoOrdemNaoExiste() {
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(99, 5, 4));
    }

    @Test
    void naoDeveBaixarQuandoOrdemNaoEstaEmExecucao() {
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.of(new OrdemServicoParaEstoque(99, false, 3)));

        assertThrows(OrdemServicoNaoEstaEmExecucaoException.class, () -> useCase.executar(99, 5, 4));

        verify(movimentacaoGateway, never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExiste() {
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.of(new OrdemServicoParaEstoque(99, true, 3)));
        when(itemGateway.pesquisarPorIdParaAtualizacao(5)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class, () -> useCase.executar(99, 5, 4));
    }

    @Test
    void naoDevePersistirQuandoEstoqueInsuficiente() {
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.of(new OrdemServicoParaEstoque(99, true, 3)));
        when(itemGateway.pesquisarPorIdParaAtualizacao(5)).thenReturn(Optional.of(item(1)));

        assertThrows(EstoqueInsuficienteException.class, () -> useCase.executar(99, 5, 4));

        verify(itemGateway, never()).salvar(any());
        verify(movimentacaoGateway, never()).salvar(any());
    }
}
