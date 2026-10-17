package com.kap.mechanics_api.core.itemestoque.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;

@ExtendWith(MockitoExtension.class)
class ConsultasEExclusaoDeItemEstoqueUseCaseTest {

    @Mock
    private ItemEstoqueGateway gateway;

    private ItemEstoque item(Integer id) {
        return ItemEstoque.reconstruir(id, "Filtro", "Óleo", TipoItemEstoque.PECA, BigDecimal.TEN, 5, 1, true);
    }

    @Test
    void listarDeveDevolverTodosOsItens() {
        List<ItemEstoque> itens = List.of(item(1), item(2));
        when(gateway.listarTodos()).thenReturn(itens);

        assertSame(itens, new ListarItemEstoqueUseCase(gateway).executar());
    }

    @Test
    void buscarDeveDevolverItemExistente() {
        ItemEstoque item = item(1);
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(item));

        assertSame(item, new BuscarItemEstoquePorIdUseCase(gateway).executar(1));
    }

    @Test
    void buscarDeveLancarExcecaoQuandoNaoExiste() {
        when(gateway.pesquisarPorId(9)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class,
                () -> new BuscarItemEstoquePorIdUseCase(gateway).executar(9));
    }

    @Test
    void excluirDeveRemoverItemExistente() {
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(item(1)));

        new ExcluirItemEstoqueUseCase(gateway).executar(1);

        verify(gateway).excluir(1);
    }

    @Test
    void excluirDeveLancarExcecaoSemRemoverQuandoNaoExiste() {
        when(gateway.pesquisarPorId(9)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class,
                () -> new ExcluirItemEstoqueUseCase(gateway).executar(9));

        verify(gateway, never()).excluir(9);
    }
}
