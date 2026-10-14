package com.kap.mechanics_api.core.veiculo.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
class ExcluirVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway gateway;

    @InjectMocks
    private ExcluirVeiculoUseCase useCase;

    @Test
    void deveExcluirQuandoVeiculoExiste() {
        Veiculo veiculo = Veiculo.reconstruir(1, "ABC1D23", "Honda", "Civic", 2020, LocalDateTime.of(2026, 10, 7, 10, 30));
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(veiculo));

        useCase.executar(1);

        verify(gateway).excluir(1);
    }

    @Test
    void deveLancarExcecaoSemExcluirQuandoNaoExiste() {
        when(gateway.pesquisarPorId(99)).thenReturn(Optional.empty());

        assertThrows(VeiculoNaoEncontradoException.class, () -> useCase.executar(99));

        verify(gateway, never()).excluir(any());
    }
}
