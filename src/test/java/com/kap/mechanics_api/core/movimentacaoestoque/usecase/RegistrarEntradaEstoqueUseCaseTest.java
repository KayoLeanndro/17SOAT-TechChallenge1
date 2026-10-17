package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoqueInativoException;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.ResponsavelMovimentacaoGateway;

@ExtendWith(MockitoExtension.class)
class RegistrarEntradaEstoqueUseCaseTest {

    private static final Instant AGORA = Instant.parse("2026-10-07T13:30:00Z");

    @Mock
    private ItemEstoqueGateway itemGateway;
    @Mock
    private MovimentacaoEstoqueGateway movimentacaoGateway;
    @Mock
    private ResponsavelMovimentacaoGateway responsavelGateway;

    private RegistrarEntradaEstoqueUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarEntradaEstoqueUseCase(itemGateway, movimentacaoGateway, responsavelGateway,
                Clock.fixed(AGORA, ZoneOffset.UTC));
    }

    private ItemEstoque item(boolean ativo) {
        return ItemEstoque.reconstruir(1, "Filtro", "Óleo", TipoItemEstoque.PECA, BigDecimal.TEN, 10, 2, ativo);
    }

    @Test
    void deveSomarAoSaldoGravarOLancamentoERetornarOSaldoNovo() {
        ItemEstoque item = item(true);
        when(itemGateway.pesquisarPorIdParaAtualizacao(1)).thenReturn(Optional.of(item));
        when(responsavelGateway.buscarIdPorLogin("estoque")).thenReturn(Optional.of(7));
        when(movimentacaoGateway.salvar(any(MovimentacaoEstoque.class))).thenAnswer(inv -> {
            MovimentacaoEstoque m = inv.getArgument(0);
            return MovimentacaoEstoque.reconstruir(50, m.getItemEstoqueId(), m.getTipo(), m.getQuantidade(),
                    m.getDataHora(), m.getUsuarioId(), m.getOrdemServicoId());
        });

        MovimentacaoEstoqueConsulta consulta = useCase.executar(1, 5, "estoque");

        assertEquals(15, item.getQuantidadeAtual());
        verify(itemGateway).salvar(item);
        assertEquals(50, consulta.movimentacao().getId());
        assertEquals(TipoMovimentacaoEstoque.ENTRADA, consulta.movimentacao().getTipo());
        assertEquals(7, consulta.movimentacao().getUsuarioId());
        assertEquals(LocalDateTime.of(2026, 10, 7, 13, 30), consulta.movimentacao().getDataHora());
        assertEquals("Filtro", consulta.itemEstoqueNome());
        assertEquals(15, consulta.saldoItemEstoque());
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExiste() {
        when(itemGateway.pesquisarPorIdParaAtualizacao(1)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class, () -> useCase.executar(1, 5, "estoque"));

        verify(movimentacaoGateway, never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(itemGateway.pesquisarPorIdParaAtualizacao(1)).thenReturn(Optional.of(item(true)));
        when(responsavelGateway.buscarIdPorLogin("fantasma")).thenReturn(Optional.empty());

        ResponsavelNaoEncontradoException ex = assertThrows(ResponsavelNaoEncontradoException.class,
                () -> useCase.executar(1, 5, "fantasma"));

        assertEquals("Usuario não encontrado: fantasma", ex.getMessage());
        verify(itemGateway, never()).salvar(any());
    }

    @Test
    void naoDevePersistirQuandoItemEstaInativo() {
        when(itemGateway.pesquisarPorIdParaAtualizacao(1)).thenReturn(Optional.of(item(false)));
        when(responsavelGateway.buscarIdPorLogin("estoque")).thenReturn(Optional.of(7));

        assertThrows(ItemEstoqueInativoException.class, () -> useCase.executar(1, 5, "estoque"));

        verify(itemGateway, never()).salvar(any());
        verify(movimentacaoGateway, never()).salvar(any());
    }

    @Test
    void deveGravarOLancamentoComOsDadosInformados() {
        when(itemGateway.pesquisarPorIdParaAtualizacao(1)).thenReturn(Optional.of(item(true)));
        when(responsavelGateway.buscarIdPorLogin("estoque")).thenReturn(Optional.of(7));
        when(movimentacaoGateway.salvar(any(MovimentacaoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.executar(1, 5, "estoque");

        ArgumentCaptor<MovimentacaoEstoque> captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movimentacaoGateway).salvar(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(1, captor.getValue().getItemEstoqueId());
        assertEquals(5, captor.getValue().getQuantidade());
        assertNull(captor.getValue().getOrdemServicoId());
    }
}
