package com.kap.mechanics_api.core.cliente.usecase;

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

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

@ExtendWith(MockitoExtension.class)
class AtualizarClienteUseCaseTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 7, 10, 30);
    private static final CpfCnpj CPF = new CpfCnpj("12345678909");

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private AtualizarClienteUseCase useCase;

    private Cliente clienteExistente() {
        return Cliente.reconstruir(1, "Maria", CPF, "11999990000", "maria@email.com", DATA);
    }

    @Test
    void deveAtualizarEPersistirOClienteExistente() {

        Cliente existente = clienteExistente();
        CpfCnpj novoCnpj = new CpfCnpj("76803621000175");
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(existente));
        when(gateway.salvar(existente)).thenReturn(existente);

        Cliente resultado = useCase.executar(1, "Maria Souza", novoCnpj, "11888880000", "maria.souza@email.com");

        assertEquals("Maria Souza", resultado.getNome());
        assertEquals(novoCnpj, resultado.getCpfCnpj());
        assertEquals("11888880000", resultado.getTelefone());
        assertEquals("maria.souza@email.com", resultado.getEmail());
        verify(gateway).salvar(existente);
    }

    @Test
    void deveManterCamposNaoInformadosAplicandoARegraDoDominio() {

        Cliente existente = clienteExistente();
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(existente));
        when(gateway.salvar(existente)).thenReturn(existente);

        Cliente resultado = useCase.executar(1, "  ", null, "11888880000", null);

        assertEquals("Maria", resultado.getNome());
        assertEquals(CPF, resultado.getCpfCnpj());
        assertEquals("11888880000", resultado.getTelefone());
        assertEquals("maria@email.com", resultado.getEmail());
    }

    @Test
    void deveLancarExcecaoESemSalvarQuandoClienteNaoExiste() {

        when(gateway.pesquisarPorId(99)).thenReturn(Optional.empty());

        ClienteNaoEncontradoException ex = assertThrows(
                ClienteNaoEncontradoException.class,
                () -> useCase.executar(99, "Maria", null, null, null)
        );

        assertEquals("Cliente nao encontrado com o id 99", ex.getMessage());
        verify(gateway, never()).salvar(any());
    }
}
