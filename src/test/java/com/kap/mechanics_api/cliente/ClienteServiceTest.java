package com.kap.mechanics_api.cliente;

import com.kap.mechanics_api.adapter.persistence.entity.ClienteJpaEntity;
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
}
