package com.kap.mechanics_api.usuario;

import com.kap.mechanics_api.core.usuario.domain.TipoUsuario;
import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;
import com.kap.mechanics_api.core.usuario.usecase.ExcluirUsuarioUseCase;
import com.kap.mechanics_api.core.usuario.usecase.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirUsuarioUseCaseTest {
    @Mock private UsuarioGateway usuarioGateway;
    @InjectMocks private ExcluirUsuarioUseCase excluir;

    @Test
    void deveExcluirDepoisDeConfirmarExistencia() {
        var usuario = new Usuario(1, "Pedro", "pedro", "hash", TipoUsuario.ADMIN);
        when(usuarioGateway.buscarPorId(1)).thenReturn(Optional.of(usuario));

        excluir.executar(1);

        var ordem = inOrder(usuarioGateway);
        ordem.verify(usuarioGateway).buscarPorId(1);
        ordem.verify(usuarioGateway).excluir(1);
    }

    @Test
    void naoDeveExcluirUsuarioInexistente() {
        when(usuarioGateway.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> excluir.executar(99));

        verify(usuarioGateway, never()).excluir(any());
    }
}
