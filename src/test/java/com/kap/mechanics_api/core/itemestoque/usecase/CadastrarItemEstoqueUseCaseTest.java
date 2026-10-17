package com.kap.mechanics_api.core.itemestoque.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;

@ExtendWith(MockitoExtension.class)
class CadastrarItemEstoqueUseCaseTest {

    @Mock
    private ItemEstoqueGateway gateway;

    @InjectMocks
    private CadastrarItemEstoqueUseCase useCase;

    @Test
    void deveCadastrarItemComSaldoZero() {
        ItemEstoque persistido = ItemEstoque.reconstruir(3, "Filtro", "Óleo", TipoItemEstoque.PECA,
                BigDecimal.TEN, 0, 2, true);
        when(gateway.salvar(any(ItemEstoque.class))).thenReturn(persistido);

        ItemEstoque resultado = useCase.executar("Filtro", "Óleo", TipoItemEstoque.PECA, BigDecimal.TEN, 2, true);

        assertSame(persistido, resultado);
        ArgumentCaptor<ItemEstoque> captor = ArgumentCaptor.forClass(ItemEstoque.class);
        verify(gateway).salvar(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(0, captor.getValue().getQuantidadeAtual());
    }

    @Test
    void naoDevePersistirQuandoDadosSaoInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.executar(null, "Óleo", TipoItemEstoque.PECA, BigDecimal.TEN, 2, true));

        verify(gateway, never()).salvar(any());
    }
}
