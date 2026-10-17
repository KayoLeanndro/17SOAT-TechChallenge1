package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoParaEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.QuantidadeDeItem;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEstaEmExecucaoException;

@ExtendWith(MockitoExtension.class)
class BaixarItensDaOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoEstoqueGateway ordemServicoGateway;
    @Mock
    private BaixarItemParaOrdemServicoUseCase baixarItem;

    @InjectMocks
    private BaixarItensDaOrdemServicoUseCase useCase;

    private void ordemEmExecucao() {
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.of(new OrdemServicoParaEstoque(99, true, 3)));
    }

    @Test
    void deveBaixarSomenteAsLinhasDeEstoqueDaPropriaOrdem() {
        ordemEmExecucao();
        when(ordemServicoGateway.listarLinhasDaOrdem(99)).thenReturn(List.of(
                new QuantidadeDeItem(5, 3),
                new QuantidadeDeItem(null, 1),     // linha de serviço: não baixa estoque
                new QuantidadeDeItem(6, 2)));

        useCase.executar(99);

        verify(baixarItem).executar(99, 5, 3);
        verify(baixarItem).executar(99, 6, 2);
        verify(ordemServicoGateway, never()).listarComponentesDosServicosOrcados(anyInt());
    }

    @Test
    void semLinhasProprias_devePorPecasPadraoDosServicosSomandoRepetidas() {
        ordemEmExecucao();
        when(ordemServicoGateway.listarLinhasDaOrdem(99)).thenReturn(List.of());
        when(ordemServicoGateway.listarComponentesDosServicosOrcados(99)).thenReturn(List.of(
                new QuantidadeDeItem(5, 2),
                new QuantidadeDeItem(6, 1),
                new QuantidadeDeItem(5, 3)));

        useCase.executar(99);

        verify(baixarItem).executar(99, 5, 5);
        verify(baixarItem).executar(99, 6, 1);
    }

    @Test
    void ordemSoComLinhasDeServicoNaoCaiParaOFallback() {
        ordemEmExecucao();
        when(ordemServicoGateway.listarLinhasDaOrdem(99)).thenReturn(List.of(new QuantidadeDeItem(null, 1)));

        useCase.executar(99);

        verifyNoInteractions(baixarItem);
        verify(ordemServicoGateway, never()).listarComponentesDosServicosOrcados(anyInt());
    }

    @Test
    void deveLancarExcecaoQuandoOrdemNaoExiste() {
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(99));
    }

    @Test
    void naoDeveBaixarQuandoOrdemNaoEstaEmExecucao() {
        when(ordemServicoGateway.buscarPorId(99)).thenReturn(Optional.of(new OrdemServicoParaEstoque(99, false, 3)));

        assertThrows(OrdemServicoNaoEstaEmExecucaoException.class, () -> useCase.executar(99));

        verify(baixarItem, never()).executar(any(), any(), any());
    }
}
