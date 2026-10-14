package com.kap.mechanics_api.core.veiculo.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import com.kap.mechanics_api.core.cliente.usecase.ClienteNaoEncontradoException;
import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

@ExtendWith(MockitoExtension.class)
class AtualizarVeiculoUseCaseTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 7, 10, 30);

    @Mock
    private VeiculoGateway veiculoGateway;
    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private AtualizarVeiculoUseCase useCase;

    private Veiculo existente() {
        return Veiculo.reconstruir(1, "ABC1D23", "Honda", "Civic", 2020, DATA);
    }

    @Test
    void deveAtualizarCamposEPersistirSemVincularQuandoNaoHaCliente() {
        Veiculo existente = existente();
        when(veiculoGateway.pesquisarPorId(1)).thenReturn(Optional.of(existente));
        when(veiculoGateway.salvar(existente)).thenReturn(existente);

        Veiculo resultado = useCase.executar(1, "XYZ9K88", null, "Accord", null, null);

        assertEquals("XYZ9K88", resultado.getPlaca());
        assertEquals("Honda", resultado.getMarca());
        assertEquals("Accord", resultado.getModelo());
        assertEquals(2020, resultado.getAno());
        verify(veiculoGateway, never()).vincularCliente(any(), any());
        verifyNoInteractions(clienteGateway);
    }

    @Test
    void deveVincularNovoClienteQuandoInformado() {
        Veiculo existente = existente();
        Cliente cliente = Cliente.reconstruir(7, "Maria", new CpfCnpj("12345678909"), null, null, DATA);
        when(veiculoGateway.pesquisarPorId(1)).thenReturn(Optional.of(existente));
        when(clienteGateway.pesquisarPorId(7)).thenReturn(Optional.of(cliente));
        when(veiculoGateway.salvar(existente)).thenReturn(existente);

        useCase.executar(1, null, null, null, null, 7);

        verify(veiculoGateway).salvar(existente);
        verify(veiculoGateway).vincularCliente(1, 7);
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoExiste() {
        when(veiculoGateway.pesquisarPorId(99)).thenReturn(Optional.empty());

        VeiculoNaoEncontradoException ex = assertThrows(VeiculoNaoEncontradoException.class,
                () -> useCase.executar(99, "XYZ9K88", null, null, null, null));

        assertEquals("Veiculo nao encontrado com o id 99", ex.getMessage());
        verify(veiculoGateway, never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoSemSalvarQuandoClienteNaoExiste() {
        when(veiculoGateway.pesquisarPorId(1)).thenReturn(Optional.of(existente()));
        when(clienteGateway.pesquisarPorId(99)).thenReturn(Optional.empty());

        assertThrows(ClienteNaoEncontradoException.class,
                () -> useCase.executar(1, "XYZ9K88", null, null, null, 99));

        verify(veiculoGateway, never()).salvar(any());
        verify(veiculoGateway, never()).vincularCliente(any(), any());
    }
}
