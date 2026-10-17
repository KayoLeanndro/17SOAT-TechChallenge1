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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.EstoqueInsuficienteException;
import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoParaEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.ResponsavelMovimentacaoGateway;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEstaEmExecucaoException;

@ExtendWith(MockitoExtension.class)
class RegistrarSaidaEstoqueUseCaseTest {

    @Mock
    private ItemEstoqueGateway itemGateway;
    @Mock
    private MovimentacaoEstoqueGateway movimentacaoGateway;
    @Mock
    private ResponsavelMovimentacaoGateway responsavelGateway;
    @Mock
    private OrdemServicoEstoqueGateway ordemServicoGateway;

    private RegistrarSaidaEstoqueUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarSaidaEstoqueUseCase(itemGateway, movimentacaoGateway, responsavelGateway,
                ordemServicoGateway, Clock.fixed(Instant.parse("2026-10-07T13:30:00Z"), ZoneOffset.UTC));
    }

    private ItemEstoque item(int saldo) {
        return ItemEstoque.reconstruir(1, "Filtro", "Óleo", TipoItemEstoque.PECA, BigDecimal.TEN, saldo, 2, true);
    }

    private void prepararItemUsuarioEOrdem(int saldo, boolean emExecucao) {
        when(itemGateway.pesquisarPorIdParaAtualizacao(1)).thenReturn(Optional.of(item(saldo)));
        when(responsavelGateway.buscarIdPorLogin("estoque")).thenReturn(Optional.of(7));
        when(ordemServicoGateway.buscarPorId(99))
                .thenReturn(Optional.of(new OrdemServicoParaEstoque(99, emExecucao, 3)));
    }

    @Test
    void deveSubtrairDoSaldoGravarOLancamentoERetornarOSaldoNovo() {
        prepararItemUsuarioEOrdem(10, true);
        when(movimentacaoGateway.salvar(any(MovimentacaoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));

        MovimentacaoEstoqueConsulta consulta = useCase.executar(1, 4, 99, "estoque");

        assertEquals(6, consulta.saldoItemEstoque());
        assertEquals(TipoMovimentacaoEstoque.SAIDA, consulta.movimentacao().getTipo());
        assertEquals(99, consulta.movimentacao().getOrdemServicoId());
        assertEquals(7, consulta.movimentacao().getUsuarioId());
        verify(itemGateway).salvar(any(ItemEstoque.class));
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExiste() {
        when(itemGateway.pesquisarPorIdParaAtualizacao(1)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class, () -> useCase.executar(1, 4, 99, "estoque"));
    }

    @Test
    void deveLancarExcecaoQuandoOrdemDeServicoNaoExiste() {
        when(itemGateway.pesquisarPorIdParaAtualizacao(1)).thenReturn(Optional.of(item(10)));
        when(responsavelGateway.buscarIdPorLogin("estoque")).thenReturn(Optional.of(7));
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(1, 4, 99, "estoque"));

        verify(movimentacaoGateway, never()).salvar(any());
    }

    @Test
    void naoDevePermitirSaidaParaOrdemForaDeExecucao() {
        prepararItemUsuarioEOrdem(10, false);

        assertThrows(OrdemServicoNaoEstaEmExecucaoException.class, () -> useCase.executar(1, 4, 99, "estoque"));

        verify(itemGateway, never()).salvar(any());
        verify(movimentacaoGateway, never()).salvar(any());
    }

    @Test
    void naoDevePersistirQuandoEstoqueInsuficiente() {
        prepararItemUsuarioEOrdem(2, true);

        assertThrows(EstoqueInsuficienteException.class, () -> useCase.executar(1, 4, 99, "estoque"));

        verify(itemGateway, never()).salvar(any());
        verify(movimentacaoGateway, never()).salvar(any());
    }
}
