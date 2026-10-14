package com.kap.mechanics_api.veiculo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.adapter.persistence.entity.VeiculoJpaEntity;
import com.kap.mechanics_api.core.veiculo.usecase.VeiculoNaoEncontradoException;
import com.kap.mechanics_api.repository.VeiculoRepository;
import com.kap.mechanics_api.service.VeiculoService;

@ExtendWith(MockitoExtension.class)
public class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    @Test
    void deveRetornarVeiculoQuandoExistePorId() {
        VeiculoJpaEntity entity = new VeiculoJpaEntity(1, "ABC1234", "Honda", "Civic", 2020);
        when(veiculoRepository.findById(1)).thenReturn(Optional.of(entity));

        assertSame(entity, veiculoService.pesquisarPorId(1));
        verify(veiculoRepository).findById(1);
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoExistePorId() {
        when(veiculoRepository.findById(1)).thenReturn(Optional.empty());

        VeiculoNaoEncontradoException ex = assertThrows(VeiculoNaoEncontradoException.class,
                () -> veiculoService.pesquisarPorId(1));

        assertEquals("Veiculo nao encontrado com o id 1", ex.getMessage());
    }
}
