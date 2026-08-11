package com.kap.mechanics_api.usuario;

import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.dto.usuario.*;
import com.kap.mechanics_api.dto.veiculo.*;
import com.kap.mechanics_api.enums.TipoUsuario;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.UsuarioNaoEncontradoException;
import com.kap.mechanics_api.exception.VeiculoNaoEncontradoException;
import com.kap.mechanics_api.mapper.UsuarioMapper;
import com.kap.mechanics_api.mapper.VeiculoMapper;
import com.kap.mechanics_api.repository.UsuarioRepository;
import com.kap.mechanics_api.repository.VeiculoRepository;
import com.kap.mechanics_api.service.UsuarioService;
import com.kap.mechanics_api.service.VeiculoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;


    @Test
    void deveCriarUsuarioComSucesso(){

        //Arrange
        CriacaoUsuarioRequestDTO usuarioDto = new CriacaoUsuarioRequestDTO("pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE.toString());
        Usuario usuario = new Usuario(null, "pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE);
        Usuario usuarioSalvo = new Usuario(1, "pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE);
        CriacaoUsuarioResponseDTO usuarioSalvoDto = new CriacaoUsuarioResponseDTO(1, "pedin", "pedinApelao", LocalDateTime.now(), TipoUsuario.ATENDENTE);

        when(usuarioMapper.toEntity(usuarioDto)).thenReturn(usuario);
        when(usuarioMapper.toResponseDto(usuarioSalvo)).thenReturn(usuarioSalvoDto);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        //act
        CriacaoUsuarioResponseDTO resultado = usuarioService.cadastrar(usuarioDto);

        //Assert
        assertNotNull(resultado);
        assertEquals("pedin", resultado.nome());
        assertEquals("pedinApelao", resultado.login());
        assertEquals("ATENDENTE", resultado.tipo().toString());

    }

    @Test
    void deveListarTodosOsUsuarioComSucesso(){
        //Arrange
        Usuario usuarioSalvo = new Usuario(1, "pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE);
        Usuario usuarioSalvo2 = new Usuario(2, "joao", "joaoApelao", "1234", TipoUsuario.ATENDENTE);


        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuarioSalvo);
        usuarios.add(usuarioSalvo2);

        List<ListagemUsuarioDTO> usuariosDto = new ArrayList<>();
        ListagemUsuarioDTO dto1 = new ListagemUsuarioDTO(1,"pedin", "1234", "pedinApelao", LocalDateTime.now(), TipoUsuario.ATENDENTE);
        ListagemUsuarioDTO dto2 = new ListagemUsuarioDTO(2,"joao", "1234", "joaoApelao", LocalDateTime.now(), TipoUsuario.ATENDENTE);
        usuariosDto.add(dto1);
        usuariosDto.add(dto2);

        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(usuarioMapper.toListagemUsuarioDto(usuarios)).thenReturn(usuariosDto);


        //act
        List<ListagemUsuarioDTO> resultado = usuarioService.listar();

        //Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(1, resultado.get(0).id());
        assertEquals("pedin", resultado.get(0).nome());
        assertEquals("pedinApelao", resultado.get(0).login());
        assertEquals("1234", resultado.get(0).senha());
        assertEquals("ATENDENTE", resultado.get(0).tipo().toString());

        assertEquals(2, resultado.get(1).id());
        assertEquals("joaoApelao", resultado.get(1).login());

        verify(usuarioRepository).findAll();
        verify(usuarioMapper).toListagemUsuarioDto(usuarios);
    }

    @Test
    void deveBuscarUsuarioPorId(){
        //arrange
        Optional<Usuario> usuario = Optional.of(new Usuario(1, "pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE));
        ListagemUsuarioDTO dto1 = new ListagemUsuarioDTO(1,"pedin", "1234", "pedinApelao", LocalDateTime.now(), TipoUsuario.ATENDENTE);

        when(usuarioRepository.findById(1)).thenReturn(usuario);
        when(usuarioMapper.toListagemUsuarioResponseDto(usuario.get())).thenReturn(dto1);

        //act
        ListagemUsuarioDTO resultado = usuarioService.pesquisarPorId(usuario.get().getId());

        //assert
        assertNotNull(resultado);
        assertEquals(1, resultado.id());
        assertEquals("pedin", resultado.nome());
        assertEquals("pedinApelao", resultado.login());
        assertEquals("1234", resultado.senha());
        assertEquals("ATENDENTE", resultado.tipo().toString());

        verify(usuarioRepository).findById(1);
        verify(usuarioMapper).toListagemUsuarioResponseDto(usuario.get());
    }

    @Test
    void deveDeletarUsuarioComSucesso(){

        //arrange
        Usuario usuarioSalvo = new Usuario(1, "pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioSalvo));

        //act
        usuarioService.deletar(usuarioSalvo.getId());

        //assert
        verify(usuarioRepository).findById(1);
        verify(usuarioRepository).delete(usuarioSalvo);

    }

    @Test
    void deveAtualizarVeiculoComSucesso(){

        //arrange
        Usuario usuario = new Usuario(1, "pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE);
        AtualizacaoUsuarioRequestDTO dto = new AtualizacaoUsuarioRequestDTO("pedin2", "pedinApelao2", "12345", "ATENDENTE");
        Usuario usuarioAlterado = new Usuario(1, "pedin2", "pedinApelao2", "12345", TipoUsuario.ATENDENTE);
        AtualizacaoUsuarioResponseDTO dtoResposta = new AtualizacaoUsuarioResponseDTO(1, "pedin2", "pedinApelao2", "12345", TipoUsuario.ATENDENTE);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toAtualizacaoUsuarioResponseDto(usuarioAlterado)).thenReturn(dtoResposta);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAlterado);

        //act
        AtualizacaoUsuarioResponseDTO resposta = usuarioService.atualizar(usuario.getId(),dto);


        //assert
        assertNotNull(resposta);
        assertEquals(1, resposta.id());
        assertEquals("pedin2", resposta.nome());
        assertEquals("pedinApelao2", resposta.login());
        assertEquals("12345", resposta.senha());
        assertEquals("ATENDENTE", resposta.tipo().toString());

        verify(usuarioRepository).findById(1);
        verify(usuarioMapper).toAtualizacaoUsuarioResponseDto(usuarioAlterado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveRetornarExceptionQuandoUsuarioNaoEncontrado(){

        //Arrange
        Usuario usuario = new Usuario(1, "pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE);
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        //act
        UsuarioNaoEncontradoException exception = assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.buscarPorId(1));

        //assert
        assertEquals("Usuario nao encontrado com o id 1", exception.getMessage());
        verify(usuarioRepository).findById(1);
    }

    @Test
    void deveRetornarExceptionQuandoNaoInformadoNenhumParametroParaAtualizacaoUsuario(){

        //arrange
        AtualizacaoUsuarioRequestDTO dto = new AtualizacaoUsuarioRequestDTO("", "", "", "");

        //act
        NenhumCampoInformadoException exception = assertThrows(
                NenhumCampoInformadoException.class,
                () -> usuarioService.atualizar(1,dto)
        );

        //assertion
        assertEquals("Nenhum campo foi informado para atualização", exception.getMessage());
        verifyNoInteractions(usuarioRepository);

    }
}
