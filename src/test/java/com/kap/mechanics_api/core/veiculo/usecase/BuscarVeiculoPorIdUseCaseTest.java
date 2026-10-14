package com.kap.mechanics_api.core.veiculo.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

@ExtendWith(MockitoExtension.class)
class BuscarVeiculoPorIdUseCaseTest {

    @Mock
    private VeiculoGateway gateway;

    @InjectMocks
    private BuscarVeiculoPorIdUseCase useCase;

    @Test
    void deveRetornarVeiculoQuandoExiste() {
        Veiculo veiculo = Veiculo.reconstruir(1, "ABC1D23", "Honda", "Civic", 2020, LocalDateTime.of(2026, 10, 7, 10, 30));
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(veiculo));

        assertSame(veiculo, useCase.executar(1));
    }

    @Test
    void deveLancarExcecaoQuandoNaoExiste() {
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.empty());

        VeiculoNaoEncontradoException ex = assertThrows(VeiculoNaoEncontradoException.class, () -> useCase.executar(1));

        assertEquals("Veiculo nao encontrado com o id 1", ex.getMessage());
    }
}
