package com.kap.mechanics_api.core.ordemservico.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.BaixarItemParaOrdemServicoUseCase;
import com.kap.mechanics_api.core.ordemservico.domain.HistoricoDeStatus;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServicoNaoEstaEmExecucaoException;
import com.kap.mechanics_api.core.ordemservico.domain.StatusOrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;
import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ComponenteDeServico;
import com.kap.mechanics_api.core.servico.gateway.ComponentesDeServicoGateway;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

@ExtendWith(MockitoExtension.class)
class IncluirItemNaOrdemServicoUseCaseTest {

    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 10, 16, 9, 0);

    @Mock private OrdemServicoGateway ordemServicoGateway;
    @Mock private ServicoGateway servicoGateway;
    @Mock private ItemEstoqueGateway itemEstoqueGateway;
    @Mock private ComponentesDeServicoGateway componentesGateway;
    @Mock private BaixarItemParaOrdemServicoUseCase baixarItem;

    @InjectMocks
    private IncluirItemNaOrdemServicoUseCase useCase;

    private OrdemServico ordem(StatusOrdemServico status) {
        return OrdemServico.reconstruir(9, 5, 3, status, ABERTURA, null,
                List.of(HistoricoDeStatus.reconstruir(1L, status, ABERTURA, null)), List.of());
    }

    private ItemEstoque item(int id, String valor) {
        return ItemEstoque.reconstruir(id, "Item " + id, "Descrição", TipoItemEstoque.PECA, new BigDecimal(valor),
                10, 1, true);
    }

    @Test
    void deveIncluirItemDeEstoqueGravarEBaixarMovimentacao() {
        when(ordemServicoGateway.pesquisarPorId(9)).thenReturn(Optional.of(ordem(StatusOrdemServico.EM_EXECUCAO)));
        when(itemEstoqueGateway.pesquisarPorId(7)).thenReturn(Optional.of(item(7, "25.00")));

        useCase.executar(9, null, 7, 2);

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);
        InOrder ordemDasChamadas = inOrder(ordemServicoGateway, baixarItem);
        ordemDasChamadas.verify(ordemServicoGateway).salvar(captor.capture());
        ordemDasChamadas.verify(baixarItem).executar(9, 7, 2);
        assertEquals(1, captor.getValue().getItens().size());
        assertEquals(0, new BigDecimal("25.00").compareTo(captor.getValue().getItens().getFirst().getValorUnitario()));
    }

    @Test
    void deveIncluirServicoEExplodirSuasPecasBaixandoCadaUma() {
        when(ordemServicoGateway.pesquisarPorId(9)).thenReturn(Optional.of(ordem(StatusOrdemServico.EM_EXECUCAO)));
        when(servicoGateway.pesquisarPorId(10)).thenReturn(Optional.of(
                Servico.reconstruir(10, "Revisão", "Completa", new BigDecimal("100.00"), 60, true)));
        when(componentesGateway.listarPorServico(10))
                .thenReturn(List.of(new ComponenteDeServico(7, 3), new ComponenteDeServico(8, 1)));
        when(itemEstoqueGateway.pesquisarPorId(7)).thenReturn(Optional.of(item(7, "10.00")));
        when(itemEstoqueGateway.pesquisarPorId(8)).thenReturn(Optional.of(item(8, "5.00")));

        useCase.executar(9, 10, null, 2);

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);
        verify(ordemServicoGateway).salvar(captor.capture());
        assertEquals(3, captor.getValue().getItens().size()); // serviço + 2 peças
        verify(baixarItem).executar(9, 7, 6);
        verify(baixarItem).executar(9, 8, 2);
    }

    @Test
    void naoDeveIncluirQuandoAOrdemNaoEstaEmExecucao() {
        when(ordemServicoGateway.pesquisarPorId(9)).thenReturn(Optional.of(ordem(StatusOrdemServico.EM_DIAGNOSTICO)));

        assertThrows(OrdemServicoNaoEstaEmExecucaoException.class, () -> useCase.executar(9, null, 7, 1));

        verify(ordemServicoGateway, never()).salvar(any());
        verifyNoInteractions(baixarItem);
    }

    @Test
    void deveExigirExatamenteUmaReferencia() {
        when(ordemServicoGateway.pesquisarPorId(9)).thenReturn(Optional.of(ordem(StatusOrdemServico.EM_EXECUCAO)));

        assertThrows(IllegalArgumentException.class, () -> useCase.executar(9, null, null, 1));
        assertThrows(IllegalArgumentException.class, () -> useCase.executar(9, 10, 7, 1));

        verify(ordemServicoGateway, never()).salvar(any());
    }

    @Test
    void deveFalharQuandoAOrdemNaoExiste() {
        when(ordemServicoGateway.pesquisarPorId(9)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(9, null, 7, 1));
    }
}
