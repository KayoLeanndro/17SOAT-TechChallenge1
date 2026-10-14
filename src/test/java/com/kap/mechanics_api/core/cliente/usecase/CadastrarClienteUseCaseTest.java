package com.kap.mechanics_api.core.cliente.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

@ExtendWith(MockitoExtension.class)
class CadastrarClienteUseCaseTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 7, 10, 30);

    @Mock
    private ClienteGateway gateway;

    private CadastrarClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        Clock clockFixo = Clock.fixed(Instant.parse("2026-10-07T10:30:00Z"), ZoneOffset.UTC);
        useCase = new CadastrarClienteUseCase(gateway, clockFixo);
    }

    @Test
    void deveCadastrarClienteComDataDoClockEIdNulo() {

        CpfCnpj cpf = new CpfCnpj("12345678909");
        when(gateway.salvar(org.mockito.ArgumentMatchers.any(Cliente.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        useCase.executar("Maria", cpf, "11999990000", "maria@email.com");

        // captura o que o caso de uso entregou ao gateway para inspecionar o objeto
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(gateway).salvar(captor.capture());
        Cliente enviado = captor.getValue();

        assertNull(enviado.getId());
        assertEquals("Maria", enviado.getNome());
        assertEquals(cpf, enviado.getCpfCnpj());
        assertEquals("11999990000", enviado.getTelefone());
        assertEquals("maria@email.com", enviado.getEmail());
        assertEquals(AGORA, enviado.getDataCriacao());
    }

    @Test
    void deveRetornarOClienteDevolvidoPeloGateway() {

        CpfCnpj cpf = new CpfCnpj("12345678909");
        Cliente salvo = Cliente.reconstruir(10, "Maria", cpf, "11999990000", "maria@email.com", AGORA);
        when(gateway.salvar(org.mockito.ArgumentMatchers.any(Cliente.class))).thenReturn(salvo);

        Cliente resultado = useCase.executar("Maria", cpf, "11999990000", "maria@email.com");

        assertSame(salvo, resultado);
        assertEquals(10, resultado.getId());
    }

    @Test
    void naoDeveChamarGatewayQuandoDominioRejeitaOsDados() {

        CpfCnpj cpf = new CpfCnpj("12345678909");

        assertThrows(IllegalArgumentException.class,
                () -> useCase.executar(" ", cpf, "11999990000", "maria@email.com"));

        org.mockito.Mockito.verifyNoInteractions(gateway);
    }
}
