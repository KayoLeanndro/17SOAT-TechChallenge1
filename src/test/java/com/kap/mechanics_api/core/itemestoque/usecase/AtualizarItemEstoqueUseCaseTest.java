package com.kap.mechanics_api.core.itemestoque.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;

@ExtendWith(MockitoExtension.class)
class AtualizarItemEstoqueUseCaseTest {

    @Mock
    private ItemEstoqueGateway gateway;

    @InjectMocks
    private AtualizarItemEstoqueUseCase useCase;

    @Test
    void deveAtualizarCamposInformadosEPersistir() {
        ItemEstoque existente = ItemEstoque.reconstruir(1, "Filtro", "Óleo", TipoItemEstoque.PECA,
                BigDecimal.TEN, 8, 2, true);
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(existente));
        when(gateway.salvar(existente)).thenReturn(existente);

        ItemEstoque resultado = useCase.executar(1, "Filtro novo", null, null, new BigDecimal("12"), null, null);

        assertEquals("Filtro novo", resultado.getNome());
        assertEquals(new BigDecimal("12"), resultado.getValorUnitario());
        assertEquals(8, resultado.getQuantidadeAtual());
        verify(gateway).salvar(existente);
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExiste() {
        when(gateway.pesquisarPorId(99)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class,
                () -> useCase.executar(99, "Novo", null, null, null, null, null));

        verify(gateway, never()).salvar(any());
    }
}
