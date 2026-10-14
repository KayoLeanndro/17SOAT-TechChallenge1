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
class BuscarClientePorDocumentoUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private BuscarClientePorDocumentoUseCase useCase;

    @Test
    void deveRetornarClienteQuandoDocumentoExiste() {

        CpfCnpj cpf = new CpfCnpj("12345678909");
        Cliente existente = Cliente.reconstruir(1, "Maria", cpf, null, null,
                LocalDateTime.of(2026, 10, 7, 10, 30));
        when(gateway.buscarPorDocumento(cpf)).thenReturn(Optional.of(existente));

        Cliente resultado = useCase.executar(cpf);

        assertSame(existente, resultado);
    }

    @Test
    void deveLancarExcecaoComODocumentoQuandoNaoExiste() {

        CpfCnpj cpf = new CpfCnpj("123.456.789-09");
        when(gateway.buscarPorDocumento(cpf)).thenReturn(Optional.empty());

        ClienteNaoEncontradoException ex = assertThrows(
                ClienteNaoEncontradoException.class,
                () -> useCase.executar(cpf)
        );

        // a mensagem usa o documento normalizado (só dígitos)
        assertEquals("Cliente nao encontrado com o documento 12345678909", ex.getMessage());
    }
}
