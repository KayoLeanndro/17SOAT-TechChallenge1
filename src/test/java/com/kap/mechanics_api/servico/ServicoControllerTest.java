package com.kap.mechanics_api.servico;

import com.kap.mechanics_api.controller.ServicoController;
import com.kap.mechanics_api.dto.servico.*;
import com.kap.mechanics_api.service.ServicoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoControllerTest {

    @Mock
    private ServicoService servicoService;

    @InjectMocks
    private ServicoController servicoController;


    @Test
    void deveCadastrarServico() {

        // Arrange
        CriacaoServicoRequestDTO request =
                new CriacaoServicoRequestDTO(
                        "Troca de óleo",
                        "Troca de óleo do motor",
                        BigDecimal.valueOf(150),
                        60,
                        true
                );

        ServicoResponseDTO response =
                new ServicoResponseDTO(  "Troca de oleo",
                         "Troca de oleo",
                        BigDecimal.valueOf(150.00),
                        Integer.valueOf("60"),
                        true
                );

        when(servicoService.cadastrar(any(CriacaoServicoRequestDTO.class)))
                .thenReturn(response);

        // Act
        ResponseEntity<ServicoResponseDTO> result =
                servicoController.cadastrar(request);

        // Assert
        assertEquals(201, result.getStatusCode().value());
        assertEquals(response, result.getBody());

        verify(servicoService, times(1))
                .cadastrar(request);
    }


    @Test
    void deveListarServicos() {

        // Arrange
        List<ServicoResponseDTO> response = List.of(
                new ServicoResponseDTO(
                        "Troca de óleo",
                        "Troca de óleo do motor",
                        BigDecimal.valueOf(150),
                        60,
                        true
                ),
                new ServicoResponseDTO(
                        "Alinhamento",
                        "Alinhamento e balanceamento",
                        BigDecimal.valueOf(200),
                        90,
                        true
                )
        );

        when(servicoService.listar())
                .thenReturn(response);

        // Act
        ResponseEntity<List<ServicoResponseDTO>> result =
                servicoController.listar();

        // Assert
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());

        verify(servicoService, times(1))
                .listar();
    }


    @Test
    void deveBuscarServicoPorId() {

        // Arrange
        Integer id = 1;

        ServicoResponseDTO response =
                new ServicoResponseDTO(
                        "Troca de óleo",
                        "Troca de óleo do motor",
                        BigDecimal.valueOf(150),
                        60,
                        true
                );

        when(servicoService.buscarPorId(id))
                .thenReturn(response);

        // Act
        ResponseEntity<ServicoResponseDTO> result =
                servicoController.buscarPorId(id);

        // Assert
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());

        verify(servicoService, times(1))
                .buscarPorId(id);
    }


    @Test
    void deveAtualizarServico() {

        // Arrange
        Integer id = 1;

        AtualizacaoServicoRequestDTO request =
                new AtualizacaoServicoRequestDTO(
                        "Troca de óleo premium",
                        "Troca completa do óleo e filtro",
                        BigDecimal.valueOf(200),
                        90,
                        true
                );

        ServicoResponseDTO response =
                new ServicoResponseDTO(
                        "Troca de óleo premium",
                        "Troca completa do óleo e filtro",
                        BigDecimal.valueOf(200),
                        90,
                        true
                );

        when(servicoService.atualizar(
                any(AtualizacaoServicoRequestDTO.class),
                eq(id)
        )).thenReturn(response);

        // Act
        ResponseEntity<ServicoResponseDTO> result =
                servicoController.atualizar(request, id);

        // Assert
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());

        verify(servicoService, times(1))
                .atualizar(request, id);
    }


    @Test
    void deveDeletarServico() {

        // Arrange
        Integer id = 1;

        doNothing()
                .when(servicoService)
                .deletar(id);

        // Act
        ResponseEntity<Void> result =
                servicoController.deletar(id);

        // Assert
        assertEquals(204, result.getStatusCode().value());
        assertNull(result.getBody());

        verify(servicoService, times(1))
                .deletar(id);
    }
}

