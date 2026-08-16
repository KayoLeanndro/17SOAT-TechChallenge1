package com.kap.mechanics_api.veiculo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kap.mechanics_api.Utilities;
import com.kap.mechanics_api.controller.VeiculoController;
import com.kap.mechanics_api.dto.veiculo.AtualizacaoVeiculoRequestDTO;
import com.kap.mechanics_api.dto.veiculo.CriacaoVeiculoRequestDTO;
import com.kap.mechanics_api.dto.veiculo.ListagemVeiculoResponseDTO;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.VeiculoNaoEncontradoException;
import com.kap.mechanics_api.service.VeiculoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VeiculoController.class)
public class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VeiculoService veiculoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String endPoint = "/api/veiculo";


    @Test
    void deveCriarVeiculoERetornar201() throws Exception {

        //act
        when(veiculoService.cadastrar(any(CriacaoVeiculoRequestDTO.class))).thenReturn(Utilities.produzirVeiculoSalvoDto());

        //assertions
        mockMvc.perform(post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Utilities.produzirVeiculoRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Honda"))
                .andExpect(jsonPath("$.modelo").value("Civic"))
                .andExpect(jsonPath("$.ano").value(2020));

    }

    @Test
    void deveRetornar200QuandoBuscarVeiculoPorPlacaExistente() throws Exception {

        //act
        when(veiculoService.buscarPorId(1)).thenReturn(Utilities.produzirRespostaListagemVeiculoDto());

        //assertion
        mockMvc.perform(get(endPoint+"/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Honda"))
                .andExpect(jsonPath("$.modelo").value("Civic"))
                .andExpect(jsonPath("$.ano").value(2020));
    }

    @Test
    void deveRetornar200QuandoListarTodosOsVeiculos() throws Exception {

        //arrange
        List<ListagemVeiculoResponseDTO> veiculosDto = Utilities.produzirListaVeiculosResponse();

        //act
        when(veiculoService.listar()).thenReturn(veiculosDto);

        //assertion
        mockMvc.perform(get(endPoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));


    }

    @Test
    void deveRetornar404QuandoNaoEncontradoVeiculoPorId() throws Exception{

        when(veiculoService.buscarPorId(99999)).thenThrow(new VeiculoNaoEncontradoException(99999));

        mockMvc.perform(get(endPoint+"/99999"))
                .andExpect(status().isNotFound());

    }

    @Test
    void deveRetornar400QuandoNaoPassadoNenhumParametroParaAtualizacao() throws Exception{
        when(veiculoService.atualizar(Utilities.produzirAtualizacaoVeiculoDtoInvalido(),9999)).thenThrow(new NenhumCampoInformadoException(AtualizacaoVeiculoRequestDTO.class));

        mockMvc.perform(put(endPoint +"/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Utilities.produzirAtualizacaoVeiculoDtoInvalido())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar201QuandoDeletadoComSucesso() throws Exception{
        doNothing().when(veiculoService).deletar(999);

        mockMvc.perform(delete( endPoint+ "/999"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar200QuandoAtualizadoComSucesso() throws Exception {

        when(veiculoService.atualizar(Utilities.produzirAtualizacaoVeiculoDto(), 99)).thenReturn(Utilities.produzirAtualizacaoVeiculoResponseDto());

        mockMvc.perform(put(endPoint +"/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Utilities.produzirAtualizacaoVeiculoDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Honda2"))
                .andExpect(jsonPath("$.modelo").value("Civic2"))
                .andExpect(jsonPath("$.ano").value(2020));
    }
}
