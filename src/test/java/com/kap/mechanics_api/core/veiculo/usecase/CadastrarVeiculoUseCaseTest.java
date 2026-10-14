package com.kap.mechanics_api.core.veiculo.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;
import com.kap.mechanics_api.core.cliente.usecase.ClienteNaoEncontradoException;
import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

@ExtendWith(MockitoExtension.class)
class CadastrarVeiculoUseCaseTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-07T10:30:00Z"), ZoneOffset.UTC);
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 7, 10, 30);

    @Mock
    private VeiculoGateway veiculoGateway;
    @Mock
    private ClienteGateway clienteGateway;

    private CadastrarVeiculoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarVeiculoUseCase(veiculoGateway, clienteGateway, CLOCK);
    }

    private Cliente cliente() {
        return Cliente.reconstruir(5, "Maria", new CpfCnpj("12345678909"), null, null, AGORA);
    }

    @Test
    void deveSalvarVeiculoComDataDoRelogioEVincularAoCliente() {
        when(clienteGateway.pesquisarPorId(5)).thenReturn(Optional.of(cliente()));
        when(veiculoGateway.salvar(any(Veiculo.class))).thenAnswer(inv -> {
            Veiculo v = inv.getArgument(0);
            return Veiculo.reconstruir(10, v.getPlaca(), v.getMarca(), v.getModelo(), v.getAno(), v.getDataCriacao());
        });

        Veiculo resultado = useCase.executar("ABC1D23", "Honda", "Civic", 2020, 5);

        assertEquals(10, resultado.getId());
        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        InOrder ordem = inOrder(veiculoGateway);
        ordem.verify(veiculoGateway).salvar(captor.capture());
        ordem.verify(veiculoGateway).vincularCliente(10, 5);
        assertNull(captor.getValue().getId());
        assertEquals(AGORA, captor.getValue().getDataCriacao());
    }

    @Test
    void deveLancarExcecaoSemGravarNadaQuandoClienteNaoExiste() {
        when(clienteGateway.pesquisarPorId(99)).thenReturn(Optional.empty());

        ClienteNaoEncontradoException ex = assertThrows(ClienteNaoEncontradoException.class,
                () -> useCase.executar("ABC1D23", "Honda", "Civic", 2020, 99));

        assertEquals("Cliente nao encontrado com o id 99", ex.getMessage());
        verify(veiculoGateway, never()).salvar(any());
        verify(veiculoGateway, never()).vincularCliente(any(), any());
    }
}
