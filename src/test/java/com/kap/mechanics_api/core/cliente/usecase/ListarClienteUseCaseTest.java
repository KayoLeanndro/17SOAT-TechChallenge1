package com.kap.mechanics_api.core.cliente.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

@ExtendWith(MockitoExtension.class)
class ListarClienteUseCaseTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 7, 10, 30);

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private ListarClienteUseCase useCase;

    @Test
    void deveRetornarTodosOsClientesDoGateway() {

        Cliente maria = Cliente.reconstruir(1, "Maria", new CpfCnpj("12345678909"), null, null, DATA);
        Cliente joao = Cliente.reconstruir(2, "João", new CpfCnpj("76803621000175"), null, null, DATA);
        when(gateway.listarTodos()).thenReturn(List.of(maria, joao));

        List<Cliente> resultado = useCase.executar();

        assertEquals(List.of(maria, joao), resultado);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaClientes() {

        when(gateway.listarTodos()).thenReturn(List.of());

        assertTrue(useCase.executar().isEmpty());
    }
}
