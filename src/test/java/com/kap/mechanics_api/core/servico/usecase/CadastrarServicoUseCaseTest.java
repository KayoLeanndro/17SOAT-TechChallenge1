package com.kap.mechanics_api.core.servico.usecase;

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

import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

@ExtendWith(MockitoExtension.class)
class CadastrarServicoUseCaseTest {

    @Mock
    private ServicoGateway gateway;

    @InjectMocks
    private CadastrarServicoUseCase useCase;

    @Test
    void deveCriarServicoNovoEPersistir() {
        Servico persistido = Servico.reconstruir(5, "Alinhamento", "3D", BigDecimal.TEN, 60, true);
        when(gateway.salvar(any(Servico.class))).thenReturn(persistido);

        Servico resultado = useCase.executar("Alinhamento", "3D", BigDecimal.TEN, 60, true);

        assertSame(persistido, resultado);
        ArgumentCaptor<Servico> captor = ArgumentCaptor.forClass(Servico.class);
        verify(gateway).salvar(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals("Alinhamento", captor.getValue().getNome());
    }

    @Test
    void naoDevePersistirQuandoDadosSaoInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.executar(" ", "3D", BigDecimal.TEN, 60, true));

        verify(gateway, never()).salvar(any());
    }
}
