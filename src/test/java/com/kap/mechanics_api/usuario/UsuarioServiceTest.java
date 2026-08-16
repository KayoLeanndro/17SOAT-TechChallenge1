package com.kap.mechanics_api.usuario;

import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.dto.usuario.*;
import com.kap.mechanics_api.dto.veiculo.*;
import com.kap.mechanics_api.enums.TipoUsuario;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.UsuarioNaoEncontradoException;
import com.kap.mechanics_api.mapper.UsuarioMapper;
import com.kap.mechanics_api.repository.UsuarioRepository;
import com.kap.mechanics_api.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;


    @Test
    void deveCriarUsuarioComSucesso(){

        CriacaoUsuarioRequestDTO usuarioDto =
                new CriacaoUsuarioRequestDTO(
                        "pedin",
                        "pedinApelao",
                        "1234",
                        TipoUsuario.ATENDENTE.toString()
                );

        Usuario usuario =
                new Usuario(
                        null,
                        "pedin",
                        "pedinApelao",
                        "1234",
                        TipoUsuario.ATENDENTE
                );

        Usuario usuarioSalvo =
                new Usuario(
                        1,
                        "pedin",
                        "pedinApelao",
                        "senhaCriptografada",
                        TipoUsuario.ATENDENTE
                );

        CriacaoUsuarioResponseDTO usuarioSalvoDto =
                new CriacaoUsuarioResponseDTO(
                        1,
                        "pedin",
                        "pedinApelao",
                        LocalDateTime.now(),
                        TipoUsuario.ATENDENTE
                );

        when(usuarioMapper.toEntity(usuarioDto))
                .thenReturn(usuario);

        when(passwordEncoder.encode("1234"))
                .thenReturn("senhaCriptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioSalvo);

        when(usuarioMapper.toResponseDto(usuarioSalvo))
                .thenReturn(usuarioSalvoDto);

        CriacaoUsuarioResponseDTO resultado =
                usuarioService.cadastrar(usuarioDto);

        assertNotNull(resultado);
        assertEquals("pedin", resultado.nome());
        assertEquals("pedinApelao", resultado.login());
        assertEquals("ATENDENTE", resultado.tipo().toString());

        verify(passwordEncoder).encode("1234");
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
    void deveAtualizarUsuarioComSucesso(){

        Usuario usuario =
                new Usuario(
                        1,
                        "pedin",
                        "pedinApelao",
                        "senhaAntiga",
                        TipoUsuario.ATENDENTE
                );

        AtualizacaoUsuarioRequestDTO dto =
                new AtualizacaoUsuarioRequestDTO(
                        "pedin2",
                        "pedinApelao2",
                        "12345",
                        "ATENDENTE"
                );

        Usuario usuarioAlterado =
                new Usuario(
                        1,
                        "pedin2",
                        "pedinApelao2",
                        "senhaNovaCriptografada",
                        TipoUsuario.ATENDENTE
                );

        AtualizacaoUsuarioResponseDTO dtoResposta =
                new AtualizacaoUsuarioResponseDTO(
                        1,
                        "pedin2",
                        "pedinApelao2",
                        "senhaNovaCriptografada",
                        TipoUsuario.ATENDENTE
                );

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.encode("12345"))
                .thenReturn("senhaNovaCriptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioAlterado);

        when(usuarioMapper.toAtualizacaoUsuarioResponseDto(usuarioAlterado))
                .thenReturn(dtoResposta);

        AtualizacaoUsuarioResponseDTO resposta =
                usuarioService.atualizar(usuario.getId(), dto);

        assertNotNull(resposta);
        assertEquals(1, resposta.id());
        assertEquals("pedin2", resposta.nome());

        verify(passwordEncoder).encode("12345");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveRetornarExceptionQuandoUsuarioNaoEncontrado() {

        // Arrange
        when(usuarioRepository.findById(1))
                .thenReturn(Optional.empty());

        // Act
        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarPorId(1)
        );

        // Assert
        assertEquals(
                "Usuario não encontrado de id: 1",
                exception.getMessage()
        );

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
