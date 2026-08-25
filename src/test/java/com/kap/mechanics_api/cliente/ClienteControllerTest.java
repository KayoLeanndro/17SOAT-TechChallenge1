package com.kap.mechanics_api.cliente;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kap.mechanics_api.Utilities;
import com.kap.mechanics_api.controller.ClienteController;
import com.kap.mechanics_api.dto.cliente.AtualizacaoClienteRequestDTO;
import com.kap.mechanics_api.dto.cliente.CriacaoClienteRequestDTO;
import com.kap.mechanics_api.dto.cliente.ListagemClienteResponseDTO;
import com.kap.mechanics_api.exception.ClienteNaoEncontradoException;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String endPoint = "/api/cliente";

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarClienteERetornar201() throws Exception {

        //act
        when(clienteService.salvar(any(CriacaoClienteRequestDTO.class))).thenReturn(Utilities.produzirClienteSalvoDto());

        //assertions
        mockMvc.perform(post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Utilities.produzirClienteRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678900"))
                .andExpect(jsonPath("$.telefone").value("51999999999"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar200QuandoBuscarClientePorIdExistente() throws Exception {

        //act
        when(clienteService.buscarPorId(1)).thenReturn(Utilities.produzirRespostaListagemClienteDto());

        //assertion
        mockMvc.perform(get(endPoint + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678900"))
                .andExpect(jsonPath("$.telefone").value("51999999999"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar200QuandoListarTodosOsClientes() throws Exception {

        //arrange
        List<ListagemClienteResponseDTO> clientesDto = Utilities.produzirListaClientesResponse();

        //act
        when(clienteService.listar()).thenReturn(clientesDto);

        //assertion
        mockMvc.perform(get(endPoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404QuandoNaoEncontradoClientePorId() throws Exception{

        when(clienteService.buscarPorId(99999)).thenThrow(new ClienteNaoEncontradoException(99999));

        mockMvc.perform(get(endPoint + "/99999"))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar400QuandoNaoPassadoNenhumParametroParaAtualizacao() throws Exception{
        when(clienteService.atualizar(Utilities.produzirAtualizacaoClienteDtoInvalido(), 9999))
                .thenThrow(new NenhumCampoInformadoException(AtualizacaoClienteRequestDTO.class));

        mockMvc.perform(put(endPoint + "/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Utilities.produzirAtualizacaoClienteDtoInvalido())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar204QuandoDeletadoComSucesso() throws Exception{
        doNothing().when(clienteService).deletar(999);

        mockMvc.perform(delete(endPoint + "/999"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar200QuandoAtualizadoComSucesso() throws Exception {

        when(clienteService.atualizar(Utilities.produzirAtualizacaoClienteDto(), 99))
                .thenReturn(Utilities.produzirAtualizacaoClienteResponseDto());

        mockMvc.perform(put(endPoint + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Utilities.produzirAtualizacaoClienteDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Junior"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678900"))
                .andExpect(jsonPath("$.telefone").value("51988887777"))
                .andExpect(jsonPath("$.email").value("joaojr@email.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar200QuandoBuscarClientePorCpf() throws Exception {
        String cpf = "12345678900";
        ListagemClienteResponseDTO response = Utilities.produzirRespostaListagemClienteDto();

        when(clienteService.buscarPorDocumento(cpf)).thenReturn(response);

        mockMvc.perform(get(endPoint + "/documento/" + cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpfCnpj").value(cpf));

        verify(clienteService).buscarPorDocumento(cpf);
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    void deveRetornar200QuandoBuscarClientePorCnpj() throws Exception {
        String cnpj = "12345678000190";
        ListagemClienteResponseDTO response = new ListagemClienteResponseDTO(
                2,
                "Oficina Exemplo",
                cnpj,
                "51988888888",
                "contato@oficina.com"
        );

        when(clienteService.buscarPorDocumento(cnpj)).thenReturn(response);

        mockMvc.perform(get(endPoint + "/documento/" + cnpj))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nome").value("Oficina Exemplo"))
                .andExpect(jsonPath("$.cpfCnpj").value(cnpj));

        verify(clienteService).buscarPorDocumento(cnpj);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404QuandoClienteNaoForEncontradoPorDocumento() throws Exception {
        String documento = "12345678900";

        when(clienteService.buscarPorDocumento(documento))
                .thenThrow(new ClienteNaoEncontradoException(documento));

        mockMvc.perform(get(endPoint + "/documento/" + documento))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Cliente não encontrado"));

        verify(clienteService).buscarPorDocumento(documento);
    }
}
