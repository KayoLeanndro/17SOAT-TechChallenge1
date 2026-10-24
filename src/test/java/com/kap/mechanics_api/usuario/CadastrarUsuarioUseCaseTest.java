package com.kap.mechanics_api.usuario;

import com.kap.mechanics_api.core.usuario.domain.TipoUsuario;
import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.CriptografiaSenhaGateway;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;
import com.kap.mechanics_api.core.usuario.usecase.CadastrarUsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioUseCaseTest {
    @Mock private UsuarioGateway usuarioGateway;
    @Mock private CriptografiaSenhaGateway criptografia;
    @InjectMocks private CadastrarUsuarioUseCase cadastrar;

    @Test
    void deveSalvarHashERetornarUsuarioComIdGerado() {
        var salvo = new Usuario(7, "Pedro", "pedro", "hash", TipoUsuario.ATENDENTE);
        when(criptografia.codificar("segredo")).thenReturn("hash");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(salvo);

        var resultado = cadastrar.executar("Pedro", "pedro", "segredo", TipoUsuario.ATENDENTE);

        var captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioGateway).salvar(captor.capture());
        var enviado = captor.getValue();
        assertNull(enviado.getId());
        assertEquals("Pedro", enviado.getNome());
        assertEquals("pedro", enviado.getLogin());
        assertEquals("hash", enviado.getSenhaHash());
        assertEquals(TipoUsuario.ATENDENTE, enviado.getTipo());
        assertNotNull(enviado.getDataCriacao());
        assertSame(salvo, resultado);
        verify(criptografia).codificar("segredo");
    }

    @Test
    void naoDeveSalvarQuandoCodificacaoFalha() {
        when(criptografia.codificar("segredo")).thenThrow(new IllegalStateException("Falha ao codificar"));

        assertThrows(IllegalStateException.class,
                () -> cadastrar.executar("Pedro", "pedro", "segredo", TipoUsuario.ATENDENTE));

        verifyNoInteractions(usuarioGateway);
    }
}
