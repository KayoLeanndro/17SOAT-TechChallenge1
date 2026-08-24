package com.kap.mechanics_api.orcamento;

import com.kap.mechanics_api.controller.OrcamentoController;
import com.kap.mechanics_api.dto.orcamento.AtualizacaoStatusOrcamentoRequestDTO;
import com.kap.mechanics_api.service.OrcamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrcamentoControllerTest {

    @Mock
    private OrcamentoService orcamentoService;

    @InjectMocks
    private OrcamentoController orcamentoController;

    @Test
    void deveAtualizarStatusDoOrcamento() {
        AtualizacaoStatusOrcamentoRequestDTO request =
                new AtualizacaoStatusOrcamentoRequestDTO("APROVADO");

        ResponseEntity<String> response =
                orcamentoController.atualizarStatus(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("status do orçamento atualizado com sucesso!", response.getBody());
        verify(orcamentoService, times(1)).atualizarStatus(1, "APROVADO");
    }
}
