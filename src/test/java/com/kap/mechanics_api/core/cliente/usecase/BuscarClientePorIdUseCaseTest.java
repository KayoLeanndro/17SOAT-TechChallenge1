package com.kap.mechanics_api.core.cliente.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

@ExtendWith(MockitoExtension.class)
class BuscarClientePorIdUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private BuscarClientePorIdUseCase useCase;

    @Test
    void deveRetornarClienteQuandoExiste() {

        Cliente existente = Cliente.reconstruir(1, "Maria", new CpfCnpj("12345678909"),
                null, null, LocalDateTime.of(2026, 10, 7, 10, 30));
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(existente));

        Cliente resultado = useCase.executar(1);

        assertSame(existente, resultado);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {

        when(gateway.pesquisarPorId(99)).thenReturn(Optional.empty());

        ClienteNaoEncontradoException ex = assertThrows(
                ClienteNaoEncontradoException.class,
                () -> useCase.executar(99)
        );

        assertEquals("Cliente nao encontrado com o id 99", ex.getMessage());
    }
}
