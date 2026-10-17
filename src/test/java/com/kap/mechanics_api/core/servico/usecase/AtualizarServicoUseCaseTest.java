package com.kap.mechanics_api.core.servico.usecase;

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

import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

@ExtendWith(MockitoExtension.class)
class AtualizarServicoUseCaseTest {

    @Mock
    private ServicoGateway gateway;

    @InjectMocks
    private AtualizarServicoUseCase useCase;

    @Test
    void deveAtualizarCamposInformadosEPersistir() {
        Servico existente = Servico.reconstruir(1, "Alinhamento", "3D", new BigDecimal("100"), 60, true);
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(existente));
        when(gateway.salvar(existente)).thenReturn(existente);

        Servico resultado = useCase.executar(1, "Alinhamento premium", null, new BigDecimal("150"), null, false);

        assertEquals("Alinhamento premium", resultado.getNome());
        assertEquals("3D", resultado.getDescricao());
        assertEquals(new BigDecimal("150"), resultado.getValorMaoDeObra());
        assertEquals(60, resultado.getTempoEstimadoMin());
        assertFalse(resultado.isAtivo());
        verify(gateway).salvar(existente);
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoExiste() {
        when(gateway.pesquisarPorId(99)).thenReturn(Optional.empty());

        ServicoNaoEncontradoException ex = assertThrows(ServicoNaoEncontradoException.class,
                () -> useCase.executar(99, "Novo", null, null, null, null));

        assertEquals("Servico nao encontrado com o id 99", ex.getMessage());
        verify(gateway, never()).salvar(any());
    }
}
