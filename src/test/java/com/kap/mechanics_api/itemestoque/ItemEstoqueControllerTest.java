package com.kap.mechanics_api.itemestoque;

import com.kap.mechanics_api.controller.ItemEstoqueController;
import com.kap.mechanics_api.dto.itemestoque.AtualizacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.CriacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.ItemEstoqueResponseDTO;
import com.kap.mechanics_api.enums.TipoItemEstoque;
import com.kap.mechanics_api.service.ItemEstoqueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemEstoqueControllerTest {

    @Mock private ItemEstoqueService itemEstoqueService;

    @InjectMocks private ItemEstoqueController controller;

    @Test
    void deveCadastrarItemERetornar201ComLocation() {
        CriacaoItemEstoqueRequestDTO dto = new CriacaoItemEstoqueRequestDTO(
                "Filtro", "Filtro de óleo", TipoItemEstoque.PECA, new BigDecimal("25.00"), 2, true);
        ItemEstoqueResponseDTO response = response(7);
        when(itemEstoqueService.cadastrar(dto)).thenReturn(response);

        ResponseEntity<ItemEstoqueResponseDTO> result = controller.cadastrar(dto);

        assertEquals(201, result.getStatusCode().value());
        assertSame(response, result.getBody());
        assertEquals("/api/item-estoque/7", result.getHeaders().getLocation().toString());
    }

    @Test
    void deveListarItens() {
        when(itemEstoqueService.listar()).thenReturn(List.of(response(1), response(2)));

        ResponseEntity<List<ItemEstoqueResponseDTO>> result = controller.listar();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void deveBuscarItemPorId() {
        ItemEstoqueResponseDTO response = response(3);
        when(itemEstoqueService.buscarPorId(3)).thenReturn(response);

        ResponseEntity<ItemEstoqueResponseDTO> result = controller.buscarPorId(3);

        assertEquals(200, result.getStatusCode().value());
        assertSame(response, result.getBody());
    }

    @Test
    void deveAtualizarItem() {
        AtualizacaoItemEstoqueRequestDTO dto =
                new AtualizacaoItemEstoqueRequestDTO("Novo nome", null, null, null, null, null);
        ItemEstoqueResponseDTO response = response(4);
        when(itemEstoqueService.atualizar(any(Integer.class), any(AtualizacaoItemEstoqueRequestDTO.class)))
                .thenReturn(response);

        ResponseEntity<ItemEstoqueResponseDTO> result = controller.atualizar(4, dto);

        assertEquals(200, result.getStatusCode().value());
        assertSame(response, result.getBody());
        verify(itemEstoqueService).atualizar(4, dto);
    }

    @Test
    void deveDeletarItemERetornar204() {
        ResponseEntity<Void> result = controller.deletar(5);

        assertEquals(204, result.getStatusCode().value());
        verify(itemEstoqueService).deletar(5);
    }

    private ItemEstoqueResponseDTO response(Integer id) {
        return new ItemEstoqueResponseDTO(id, "Filtro", "Filtro de óleo", TipoItemEstoque.PECA,
                new BigDecimal("25.00"), 10, 2, true);
    }
}
