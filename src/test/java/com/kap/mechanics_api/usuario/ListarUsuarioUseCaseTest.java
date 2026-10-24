package com.kap.mechanics_api.usuario;

import com.kap.mechanics_api.core.usuario.domain.TipoUsuario;
import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;
import com.kap.mechanics_api.core.usuario.usecase.ListarUsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarUsuarioUseCaseTest {
    @Mock private UsuarioGateway usuarioGateway;
    @InjectMocks private ListarUsuarioUseCase listar;

    @Test
    void deveRetornarUsuariosDoGateway() {
        var usuarios = List.of(new Usuario(1, "Pedro", "pedro", "hash", TipoUsuario.ADMIN));
        when(usuarioGateway.listarTodos()).thenReturn(usuarios);

        assertEquals(usuarios, listar.executar());
    }

    @Test
    void deveAceitarListaVazia() {
        when(usuarioGateway.listarTodos()).thenReturn(List.of());

        assertTrue(listar.executar().isEmpty());
    }
}
