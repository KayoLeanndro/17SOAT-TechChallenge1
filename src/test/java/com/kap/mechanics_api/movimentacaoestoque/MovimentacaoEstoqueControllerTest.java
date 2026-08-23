package com.kap.mechanics_api.movimentacaoestoque;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kap.mechanics_api.controller.MovimentacaoEstoqueController;
import com.kap.mechanics_api.dto.movimentacaoestoque.MovimentacaoEstoqueResponseDTO;
import com.kap.mechanics_api.dto.movimentacaoestoque.RegistroEntradaMovimentacaoEstoqueRequestDTO;
import com.kap.mechanics_api.dto.movimentacaoestoque.RegistroSaidaMovimentacaoEstoqueRequestDTO;
import com.kap.mechanics_api.enums.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.service.MovimentacaoEstoqueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueControllerTest {

    @Mock
    private MovimentacaoEstoqueService service;

    @InjectMocks
    private MovimentacaoEstoqueController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveRegistrarEntrada() {
        MovimentacaoEstoqueResponseDTO response = response(1L, TipoMovimentacaoEstoque.ENTRADA, 10, null, 20);
        when(service.registrarEntrada(any(RegistroEntradaMovimentacaoEstoqueRequestDTO.class), any())).thenReturn(response);

        ResponseEntity<MovimentacaoEstoqueResponseDTO> result =
                controller.registrarEntrada(new RegistroEntradaMovimentacaoEstoqueRequestDTO(1, 10), autenticacao());

        assertEquals(201, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        assertEquals("/api/movimentacao-estoque/1", result.getHeaders().getLocation().toString());
    }

    @Test
    void deveRegistrarSaida() {
        MovimentacaoEstoqueResponseDTO response = response(2L, TipoMovimentacaoEstoque.SAIDA, 4, 99L, 16);
        when(service.registrarSaida(any(RegistroSaidaMovimentacaoEstoqueRequestDTO.class), any())).thenReturn(response);

        ResponseEntity<MovimentacaoEstoqueResponseDTO> result =
                controller.registrarSaida(new RegistroSaidaMovimentacaoEstoqueRequestDTO(1, 4, 99L), autenticacao());

        assertEquals(201, result.getStatusCode().value());
        assertEquals(response, result.getBody());
    }

    @Test
    void deveListarMovimentacoes() {
        when(service.listar()).thenReturn(List.of(
                response(1L, TipoMovimentacaoEstoque.ENTRADA, 10, null, 20),
                response(2L, TipoMovimentacaoEstoque.SAIDA, 3, 99L, 17)
        ));

        ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> result = controller.listar();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void deveListarMovimentacoesPorItem() {
        when(service.listarPorItem(1)).thenReturn(List.of(
                response(1L, TipoMovimentacaoEstoque.ENTRADA, 10, null, 20)
        ));

        ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> result = controller.listarPorItem(1);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void deveListarMovimentacoesPorOrdemServico() {
        when(service.listarPorOrdemServico(99L)).thenReturn(List.of(
                response(2L, TipoMovimentacaoEstoque.SAIDA, 3, 99L, 17)
        ));

        ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> result = controller.listarPorOrdemServico(99L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void deveListarMovimentacoesPorTipo() {
        when(service.listarPorTipo(TipoMovimentacaoEstoque.ENTRADA)).thenReturn(List.of(
                response(1L, TipoMovimentacaoEstoque.ENTRADA, 10, null, 20)
        ));

        ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> result = controller.listarPorTipo(TipoMovimentacaoEstoque.ENTRADA);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void deveListarMovimentacoesPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.of(2026, 8, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 8, 31, 23, 59);
        when(service.listarPorPeriodo(inicio, fim)).thenReturn(List.of(
                response(1L, TipoMovimentacaoEstoque.ENTRADA, 10, null, 20)
        ));

        ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> result = controller.listarPorPeriodo(inicio, fim);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void deveBuscarPorId() {
        MovimentacaoEstoqueResponseDTO response = response(3L, TipoMovimentacaoEstoque.ENTRADA, 7, null, 27);
        when(service.buscarPorId(3L)).thenReturn(response);

        ResponseEntity<MovimentacaoEstoqueResponseDTO> result = controller.buscarPorId(3L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
    }

    private MovimentacaoEstoqueResponseDTO response(Long id,
                                                    TipoMovimentacaoEstoque tipo,
                                                    Integer quantidade,
                                                    Long ordemServicoId,
                                                    Integer saldo) {
        return new MovimentacaoEstoqueResponseDTO(
                id,
                1,
                "Filtro",
                tipo,
                quantidade,
                LocalDateTime.of(2026, 8, 22, 10, 0),
                2,
                ordemServicoId,
                saldo
        );
    }

    private TestingAuthenticationToken autenticacao() {
        return new TestingAuthenticationToken("estoque", null);
    }
}
