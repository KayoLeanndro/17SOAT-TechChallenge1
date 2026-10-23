package com.kap.mechanics_api.usuario;

import com.kap.mechanics_api.core.usuario.domain.TipoUsuario;
import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.CriptografiaSenhaGateway;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;
import com.kap.mechanics_api.core.usuario.usecase.AtualizarUsuarioUseCase;
import com.kap.mechanics_api.core.usuario.usecase.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarUsuarioUseCaseTest {
    @Mock private UsuarioGateway usuarioGateway;
    @Mock private CriptografiaSenhaGateway criptografia;
    @InjectMocks private AtualizarUsuarioUseCase atualizar;

    private Usuario existente() {
        var usuario = new Usuario(1, "Pedro", "pedro", "hash-antigo", TipoUsuario.ATENDENTE);
        usuario.setDataCriacao(LocalDateTime.of(2026, 1, 1, 10, 0));
        when(usuarioGateway.buscarPorId(1)).thenReturn(Optional.of(usuario));
        return usuario;
    }

    @Test
    void deveAtualizarCamposPreservandoIdEData() {
        var usuario = existente();
        var data = usuario.getDataCriacao();
        when(criptografia.codificar("nova-senha")).thenReturn("hash-novo");
        when(usuarioGateway.salvar(usuario)).thenReturn(usuario);

        var resultado = atualizar.executar(1, "Maria", "maria", "nova-senha", TipoUsuario.ADMIN);

        assertSame(usuario, resultado);
        assertEquals(1, resultado.getId());
        assertEquals(data, resultado.getDataCriacao());
        assertEquals("Maria", resultado.getNome());
        assertEquals("maria", resultado.getLogin());
        assertEquals("hash-novo", resultado.getSenhaHash());
        assertEquals(TipoUsuario.ADMIN, resultado.getTipo());
        verify(criptografia).codificar("nova-senha");
    }

    @Test
    void devePreservarCamposAusentesESenhaAtual() {
        var usuario = existente();
        when(usuarioGateway.salvar(usuario)).thenReturn(usuario);

        var resultado = atualizar.executar(1, "Maria", null, null, null);

        assertEquals("Maria", resultado.getNome());
        assertEquals("pedro", resultado.getLogin());
        assertEquals("hash-antigo", resultado.getSenhaHash());
        assertEquals(TipoUsuario.ATENDENTE, resultado.getTipo());
        verifyNoInteractions(criptografia);
    }

    @Test
    void deveIgnorarTextosEmBranco() {
        var usuario = existente();
        when(usuarioGateway.salvar(usuario)).thenReturn(usuario);

        var resultado = atualizar.executar(1, "  ", "\t", " ", TipoUsuario.ADMIN);

        assertEquals("Pedro", resultado.getNome());
        assertEquals("pedro", resultado.getLogin());
        assertEquals("hash-antigo", resultado.getSenhaHash());
        assertEquals(TipoUsuario.ADMIN, resultado.getTipo());
        verifyNoInteractions(criptografia);
    }

    @Test
    void naoDeveSalvarOuCodificarSenhaQuandoUsuarioNaoExiste() {
        when(usuarioGateway.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> atualizar.executar(99, "Maria", null, "nova", null));

        verify(usuarioGateway, never()).salvar(any());
        verifyNoInteractions(criptografia);
    }

    @Test
    void naoDeveSalvarQuandoCodificacaoFalha() {
        existente();
        when(criptografia.codificar("nova")).thenThrow(new IllegalStateException("Falha ao codificar"));

        assertThrows(IllegalStateException.class,
                () -> atualizar.executar(1, null, null, "nova", null));

        verify(usuarioGateway, never()).salvar(any());
    }
}
