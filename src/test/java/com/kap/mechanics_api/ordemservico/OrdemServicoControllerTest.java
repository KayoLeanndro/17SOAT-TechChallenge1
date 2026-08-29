package com.kap.mechanics_api.ordemservico;

import com.kap.mechanics_api.controller.OrdemServicoController;
import com.kap.mechanics_api.dto.ordemservico.ListagemOrdemServicoResponseDTO;
import com.kap.mechanics_api.exception.ClienteNaoEncontradoException;
import com.kap.mechanics_api.service.OrdemServicoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoController.class)
class OrdemServicoControllerTest {

    private static final String ENDPOINT = "/api/ordem-servico";

    @Autowired private MockMvc mockMvc;
    @MockitoBean private OrdemServicoService ordemServicoService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarOrdensServicoPorIdDoCliente() throws Exception {
        when(ordemServicoService.listarPorCliente(7, null)).thenReturn(List.of(
                new ListagemOrdemServicoResponseDTO(9, 11, "EM_EXECUCAO",
                        LocalDateTime.of(2026, 8, 28, 10, 0), null)));

        mockMvc.perform(get(ENDPOINT).param("clienteId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9))
                .andExpect(jsonPath("$[0].orcamentoId").value(11))
                .andExpect(jsonPath("$[0].status").value("EM_EXECUCAO"));

        verify(ordemServicoService).listarPorCliente(7, null);
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    void deveRetornarOrdensServicoPorCpfCnpj() throws Exception {
        when(ordemServicoService.listarPorCliente(null, "12345678900")).thenReturn(List.of());

        mockMvc.perform(get(ENDPOINT).param("cpfCnpj", "12345678900"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(ordemServicoService).listarPorCliente(null, "12345678900");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404QuandoClienteNaoForEncontrado() throws Exception {
        when(ordemServicoService.listarPorCliente(99, null)).thenThrow(new ClienteNaoEncontradoException(99));

        mockMvc.perform(get(ENDPOINT).param("clienteId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Cliente não encontrado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar400QuandoFiltroForInvalido() throws Exception {
        when(ordemServicoService.listarPorCliente(null, null))
                .thenThrow(new IllegalArgumentException("Informe o ID do cliente ou CPF/CNPJ"));

        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("deve retornar 200 com todas as ordens de servico ao chamar GET /listar")
    void deveRetornarTodasAsOrdensServico() throws Exception {
        when(ordemServicoService.listar()).thenReturn(List.of(
                new ListagemOrdemServicoResponseDTO(1, 10, "AGUARDANDO_APROVACAO",
                        LocalDateTime.of(2026, 8, 20, 9, 0), null),
                new ListagemOrdemServicoResponseDTO(2, 20, "FINALIZADA",
                        LocalDateTime.of(2026, 8, 21, 14, 0),
                        LocalDateTime.of(2026, 8, 25, 17, 30))));

        mockMvc.perform(get(ENDPOINT + "/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orcamentoId").value(10))
                .andExpect(jsonPath("$[0].status").value("AGUARDANDO_APROVACAO"))
                .andExpect(jsonPath("$[0].dataAbertura").value("2026-08-20T09:00:00"))
                .andExpect(jsonPath("$[0].dataEntrega").doesNotExist())
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].orcamentoId").value(20))
                .andExpect(jsonPath("$[1].status").value("FINALIZADA"))
                .andExpect(jsonPath("$[1].dataEntrega").value("2026-08-25T17:30:00"));

        verify(ordemServicoService).listar();
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @DisplayName("deve retornar 200 com lista vazia quando nao houver ordens de servico")
    void deveRetornarListaVaziaQuandoNaoHouverOrdensServico() throws Exception {
        when(ordemServicoService.listar()).thenReturn(List.of());

        mockMvc.perform(get(ENDPOINT + "/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(ordemServicoService).listar();
    }

}
