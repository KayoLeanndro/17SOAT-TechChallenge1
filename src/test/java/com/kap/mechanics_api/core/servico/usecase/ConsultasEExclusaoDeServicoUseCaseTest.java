package com.kap.mechanics_api.core.servico.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

@ExtendWith(MockitoExtension.class)
class ConsultasEExclusaoDeServicoUseCaseTest {

    @Mock
    private ServicoGateway gateway;

    private Servico servico(Integer id) {
        return Servico.reconstruir(id, "Alinhamento", "3D", BigDecimal.TEN, 60, true);
    }

    @Test
    void listarDeveDevolverTodosOsServicos() {
        List<Servico> servicos = List.of(servico(1), servico(2));
        when(gateway.listarTodos()).thenReturn(servicos);

        assertSame(servicos, new ListarServicoUseCase(gateway).executar());
    }

    @Test
    void buscarDeveDevolverServicoExistente() {
        Servico servico = servico(1);
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(servico));

        assertSame(servico, new BuscarServicoPorIdUseCase(gateway).executar(1));
    }

    @Test
    void buscarDeveLancarExcecaoQuandoNaoExiste() {
        when(gateway.pesquisarPorId(9)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoEncontradoException.class, () -> new BuscarServicoPorIdUseCase(gateway).executar(9));
    }

    @Test
    void excluirDeveRemoverServicoExistente() {
        when(gateway.pesquisarPorId(1)).thenReturn(Optional.of(servico(1)));

        new ExcluirServicoUseCase(gateway).executar(1);

        verify(gateway).excluir(1);
    }

    @Test
    void excluirDeveLancarExcecaoSemRemoverQuandoNaoExiste() {
        when(gateway.pesquisarPorId(9)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoEncontradoException.class, () -> new ExcluirServicoUseCase(gateway).executar(9));

        verify(gateway, never()).excluir(9);
    }
}
