package com.kap.mechanics_api.core.veiculo.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

@ExtendWith(MockitoExtension.class)
class ListarVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway gateway;

    @InjectMocks
    private ListarVeiculoUseCase useCase;

    @Test
    void deveRetornarTodosOsVeiculosDoGateway() {
        LocalDateTime data = LocalDateTime.of(2026, 10, 7, 10, 30);
        List<Veiculo> veiculos = List.of(
                Veiculo.reconstruir(1, "ABC1D23", "Honda", "Civic", 2020, data),
                Veiculo.reconstruir(2, "ADD1234", "Honda", "CRV", 2022, data));
        when(gateway.listarTodos()).thenReturn(veiculos);

        assertEquals(veiculos, useCase.executar());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaVeiculos() {
        when(gateway.listarTodos()).thenReturn(List.of());

        assertTrue(useCase.executar().isEmpty());
    }
}
