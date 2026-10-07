package com.kap.mechanics_api.cliente;

import com.kap.mechanics_api.adapter.persistence.entity.ClienteJpaEntity;
import com.kap.mechanics_api.domain.ClienteVeiculo;
import com.kap.mechanics_api.domain.ClienteVeiculoId;
import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.core.cliente.usecase.ClienteNaoEncontradoException;
import com.kap.mechanics_api.repository.ClienteRepository;
import com.kap.mechanics_api.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveRetornarClienteQuandoExistePorId(){
        ClienteJpaEntity clienteJpaEntity = new ClienteJpaEntity("João Silva", "12345678900", "51999999999", "joao@email.com", LocalDateTime.now());
        clienteJpaEntity.setId(1);
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteJpaEntity));

        ClienteJpaEntity resultado = clienteService.pesquisarPorId(1);

        assertSame(clienteJpaEntity, resultado);
        verify(clienteRepository).findById(1);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExistePorId(){
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        ClienteNaoEncontradoException exception = assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.pesquisarPorId(1));

        assertEquals("Cliente nao encontrado com o id 1", exception.getMessage());
    }

    @Test
    void construtorPadraoDeveIniciarComTodosOsCamposNulos() {
        ClienteVeiculo clienteVeiculo = new ClienteVeiculo();

        assertNull(clienteVeiculo.getId());
        assertNull(clienteVeiculo.getCliente());
        assertNull(clienteVeiculo.getVeiculo());
    }

    @Test
    void construtorComArgumentosDevePreencherVeiculoECliente() {
        Veiculo veiculo = new Veiculo();
        ClienteJpaEntity clienteJpaEntity = new ClienteJpaEntity();

        ClienteVeiculo clienteVeiculo = new ClienteVeiculo(veiculo, clienteJpaEntity);

        assertEquals(veiculo, clienteVeiculo.getVeiculo());
        assertEquals(clienteJpaEntity, clienteVeiculo.getCliente());
    }

    @Test
    void construtorComArgumentosNaoDeveDefinirIdAutomaticamente() {
        // O id é um @EmbeddedId com @MapsId, então só é populado pelo
        // Hibernate no momento do persist, não pelo construtor Java puro.
        ClienteVeiculo clienteVeiculo = new ClienteVeiculo(new Veiculo(), new ClienteJpaEntity());

        assertNull(clienteVeiculo.getId());
    }

    @Test
    void setIdDeveAtualizarIdCorretamente() {
        ClienteVeiculo clienteVeiculo = new ClienteVeiculo();
        ClienteVeiculoId id = new ClienteVeiculoId(1, 10);

        clienteVeiculo.setId(id);

        assertEquals(id, clienteVeiculo.getId());
    }

    @Test
    void setClienteDeveAtualizarClienteCorretamente() {
        ClienteVeiculo clienteVeiculo = new ClienteVeiculo();
        ClienteJpaEntity clienteJpaEntity = new ClienteJpaEntity();
        clienteJpaEntity.setId(3);

        clienteVeiculo.setCliente(clienteJpaEntity);

        assertEquals(clienteJpaEntity, clienteVeiculo.getCliente());
        assertEquals(3, clienteVeiculo.getCliente().getId());
    }

    @Test
    void setVeiculoDeveAtualizarVeiculoCorretamente() {
        ClienteVeiculo clienteVeiculo = new ClienteVeiculo();
        Veiculo veiculo = new Veiculo();
        veiculo.setId(8);

        clienteVeiculo.setVeiculo(veiculo);

        assertEquals(veiculo, clienteVeiculo.getVeiculo());
        assertEquals(8, clienteVeiculo.getVeiculo().getId());
    }

    @Test
    void setClienteDevePermitirValorNulo() {
        ClienteVeiculo clienteVeiculo = new ClienteVeiculo(new Veiculo(), new ClienteJpaEntity());

        clienteVeiculo.setCliente(null);

        assertNull(clienteVeiculo.getCliente());
    }

    @Test
    void setVeiculoDevePermitirValorNulo() {
        ClienteVeiculo clienteVeiculo = new ClienteVeiculo(new Veiculo(), new ClienteJpaEntity());

        clienteVeiculo.setVeiculo(null);

        assertNull(clienteVeiculo.getVeiculo());
    }

}
