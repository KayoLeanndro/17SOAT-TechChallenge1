package com.kap.mechanics_api.usuario;

import com.kap.mechanics_api.core.usuario.domain.TipoUsuario;
import com.kap.mechanics_api.core.usuario.domain.Usuario;
import com.kap.mechanics_api.core.usuario.gateway.UsuarioGateway;
import com.kap.mechanics_api.core.usuario.usecase.BuscarPorIdUseCase;
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
class BuscarPorIdUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarPorIdUseCase buscarPorIdUseCase;

    @Test
    void deveRetornarUsuarioQuandoEncontrado() {
        var usuario = new Usuario(
                1, "Pedro", "pedro", "hash", TipoUsuario.ATENDENTE);

        when(usuarioGateway.buscarPorId(1))
                .thenReturn(Optional.of(usuario));

        var resultado = buscarPorIdUseCase.executar(1);

        assertSame(usuario, resultado);
        verify(usuarioGateway).buscarPorId(1);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioGateway.buscarPorId(99))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> buscarPorIdUseCase.executar(99)
        );
    }
}