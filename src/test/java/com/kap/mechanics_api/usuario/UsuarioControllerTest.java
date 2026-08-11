package com.kap.mechanics_api.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kap.mechanics_api.Utilities;
import com.kap.mechanics_api.controller.UsuarioController;
import com.kap.mechanics_api.controller.VeiculoController;
import com.kap.mechanics_api.dto.usuario.AtualizacaoUsuarioRequestDTO;
import com.kap.mechanics_api.dto.usuario.AtualizacaoUsuarioResponseDTO;
import com.kap.mechanics_api.dto.usuario.CriacaoUsuarioRequestDTO;
import com.kap.mechanics_api.dto.usuario.ListagemUsuarioDTO;
import com.kap.mechanics_api.dto.veiculo.AtualizacaoVeiculoRequestDTO;
import com.kap.mechanics_api.dto.veiculo.CriacaoVeiculoRequestDTO;
import com.kap.mechanics_api.dto.veiculo.ListagemVeiculoResponseDTO;
import com.kap.mechanics_api.enums.TipoUsuario;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.UsuarioNaoEncontradoException;
import com.kap.mechanics_api.exception.VeiculoNaoEncontradoException;
import com.kap.mechanics_api.service.UsuarioService;
import com.kap.mechanics_api.service.VeiculoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String endPoint = "/api/usuario";


    @Test
    void deveCriarUsuarioERetornar201() throws Exception {

        //act
        when(usuarioService.cadastrar(any(CriacaoUsuarioRequestDTO.class))).thenReturn(Utilities.produzirUsuarioSalvoDto());

        //assertions
        mockMvc.perform(post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Utilities.produzirUsuarioRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("pedin"))
                .andExpect(jsonPath("$.login").value("pedinApelao"))
                .andExpect(jsonPath("$.tipo").value(TipoUsuario.ATENDENTE.toString()));

    }

    @Test
    void deveRetornar200QuandoBuscarUsuarioPorIdExistente() throws Exception {

        //act
        when(usuarioService.pesquisarPorId(1)).thenReturn(Utilities.produzirRespostaListagemUsuarioDto());

        //assertion
        mockMvc.perform(get(endPoint+"/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("pedin"))
                .andExpect(jsonPath("$.login").value("pedinApelao"))
                .andExpect(jsonPath("$.tipo").value(TipoUsuario.ATENDENTE.toString()));
    }

    @Test
    void deveRetornar200QuandoListarTodosOsUsuarios() throws Exception {

        //arrange
        List<ListagemUsuarioDTO> usuariosDto = Utilities.produzirListaUsuariosResponse();

        //act
        when(usuarioService.listar()).thenReturn(usuariosDto);

        //assertion
        mockMvc.perform(get(endPoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));


    }

    @Test
    void deveRetornar404QuandoNaoEncontradoUsuarioPorId() throws Exception{

        when(usuarioService.pesquisarPorId(99999)).thenThrow(new UsuarioNaoEncontradoException(99999));

        mockMvc.perform(get(endPoint+"/99999"))
                .andExpect(status().isNotFound());

    }

    @Test
    void deveRetornar400QuandoNaoPassadoNenhumParametroParaAtualizacaoUsuario() throws Exception{
        when(usuarioService.atualizar(9999, Utilities.produzirAtualizacaoUsuarioDtoInvalido())).thenThrow(new NenhumCampoInformadoException(AtualizacaoUsuarioRequestDTO.class));

        mockMvc.perform(put(endPoint +"/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Utilities.produzirAtualizacaoUsuarioDtoInvalido())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar201QuandoDeletadoUsuarioComSucesso() throws Exception{
        doNothing().when(usuarioService).deletar(999);

        mockMvc.perform(delete( endPoint+ "/999"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar200QuandoUsuarioAtualizadoComSucesso() throws Exception {

        when(usuarioService.atualizar(99, Utilities.produzirAtualizacaoUsuarioDto())).thenReturn(Utilities.produzirAtualizacaoUsuarioResponseDto());

        mockMvc.perform(put(endPoint +"/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Utilities.produzirAtualizacaoUsuarioResponseDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("pedin2"))
                .andExpect(jsonPath("$.login").value("pedinApelao2"))
                .andExpect(jsonPath("$.tipo").value(TipoUsuario.ATENDENTE.toString()));
    }
}
