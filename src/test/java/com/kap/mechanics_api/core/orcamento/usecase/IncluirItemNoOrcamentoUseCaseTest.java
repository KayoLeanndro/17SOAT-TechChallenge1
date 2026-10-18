package com.kap.mechanics_api.core.orcamento.usecase;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ComponenteDeServico;
import com.kap.mechanics_api.core.servico.gateway.ComponentesDeServicoGateway;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

@ExtendWith(MockitoExtension.class)
class IncluirItemNoOrcamentoUseCaseTest {

    @Mock private OrcamentoGateway orcamentoGateway;
    @Mock private ServicoGateway servicoGateway;
    @Mock private ItemEstoqueGateway itemEstoqueGateway;
    @Mock private ComponentesDeServicoGateway componentesGateway;

    @InjectMocks
    private IncluirItemNoOrcamentoUseCase useCase;

    private Orcamento orcamento(StatusOrcamento status) {
        return Orcamento.reconstruir(5, 1, 2, BigDecimal.ZERO, status, LocalDateTime.now(), null, List.of(),
                List.of());
    }

    private ItemEstoque item(int id, String valor) {
        return ItemEstoque.reconstruir(id, "Item " + id, "Descrição", TipoItemEstoque.PECA, new BigDecimal(valor),
                10, 1, true);
    }

    @Test
    void deveIncluirItemDeEstoqueECorrigirOTotal() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(orcamento(StatusOrcamento.PENDENTE)));
        when(itemEstoqueGateway.pesquisarPorId(7)).thenReturn(Optional.of(item(7, "25.00")));

        useCase.executar(5, null, 7, 4);

        ArgumentCaptor<Orcamento> captor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoGateway).salvar(captor.capture());
        assertEquals(0, new BigDecimal("100.00").compareTo(captor.getValue().getValorTotal()));
    }

    @Test
    void deveIncluirServicoComSuasPecasPadraoMultiplicadasPelaQuantidade() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(orcamento(StatusOrcamento.PENDENTE)));
        when(servicoGateway.pesquisarPorId(10)).thenReturn(Optional.of(
                Servico.reconstruir(10, "Revisão", "Completa", new BigDecimal("100.00"), 60, true)));
        when(componentesGateway.listarPorServico(10)).thenReturn(List.of(new ComponenteDeServico(7, 3)));
        when(itemEstoqueGateway.pesquisarPorId(7)).thenReturn(Optional.of(item(7, "10.00")));

        useCase.executar(5, 10, null, 2);

        ArgumentCaptor<Orcamento> captor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoGateway).salvar(captor.capture());
        // 2 x 100 + 6 x 10
        assertEquals(0, new BigDecimal("260.00").compareTo(captor.getValue().getValorTotal()));
        assertEquals(2, captor.getValue().getItens().size());
    }

    @Test
    void naoDeveIncluirEmOrcamentoJaRespondido() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(orcamento(StatusOrcamento.APROVADO)));

        assertThrows(IllegalArgumentException.class, () -> useCase.executar(5, null, 7, 1));

        verify(orcamentoGateway, never()).salvar(any());
    }

    @Test
    void deveExigirExatamenteUmaReferencia() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(orcamento(StatusOrcamento.PENDENTE)));

        assertThrows(IllegalArgumentException.class, () -> useCase.executar(5, null, null, 1));
        assertThrows(IllegalArgumentException.class, () -> useCase.executar(5, 10, 7, 1));

        verify(orcamentoGateway, never()).salvar(any());
    }

    @Test
    void deveFalharQuandoOrcamentoNaoExiste() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> useCase.executar(5, null, 7, 1));
    }

    @Test
    void deveFalharQuandoItemDeEstoqueNaoExiste() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(orcamento(StatusOrcamento.PENDENTE)));
        when(itemEstoqueGateway.pesquisarPorId(7)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class, () -> useCase.executar(5, null, 7, 1));

        verify(orcamentoGateway, never()).salvar(any());
    }
}
