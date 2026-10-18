package com.kap.mechanics_api.core.ordemservico.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.ordemservico.domain.HistoricoDeStatus;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.StatusOrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

// O que a ordem faz quando o cliente aprova ou rejeita o orçamento.
@ExtendWith(MockitoExtension.class)
class ReacoesAoOrcamentoUseCasesTest {

    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-10-16T12:00:00Z"), ZoneOffset.UTC);
    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 10, 16, 9, 0);

    @Mock private OrdemServicoGateway ordemServicoGateway;
    @Mock private TransicionarStatusOrdemServicoUseCase transicionar;

    private OrdemServico ordemAguardando() {
        return OrdemServico.reconstruir(9, 5, 3, StatusOrdemServico.AGUARDANDO_APROVACAO, ABERTURA, null,
                List.of(HistoricoDeStatus.reconstruir(1L, StatusOrdemServico.AGUARDANDO_APROVACAO, ABERTURA, null)),
                List.of());
    }

    @Test
    void aprovacaoDeveLevarAOrdemParaDiagnostico() {
        when(ordemServicoGateway.pesquisarPorOrcamentoId(5)).thenReturn(Optional.of(ordemAguardando()));
        var useCase = new IniciarDiagnosticoPorOrcamentoUseCase(ordemServicoGateway, transicionar);

        useCase.executar(5);

        verify(transicionar).executar(9, StatusOrdemServico.EM_DIAGNOSTICO);
    }

    @Test
    void aprovacaoSemOrdemDeveFalharComOIdDoOrcamento() {
        when(ordemServicoGateway.pesquisarPorOrcamentoId(5)).thenReturn(Optional.empty());
        var useCase = new IniciarDiagnosticoPorOrcamentoUseCase(ordemServicoGateway, transicionar);

        OrdemServicoNaoEncontradaException ex =
                assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(5));

        assertTrue(ex.getMessage().contains("5"));
        verifyNoInteractions(transicionar);
    }

    @Test
    void rejeicaoDeveFinalizarAOrdemRegistrandoOHistorico() {
        when(ordemServicoGateway.pesquisarPorOrcamentoId(5)).thenReturn(Optional.of(ordemAguardando()));
        when(ordemServicoGateway.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));
        var useCase = new FinalizarOrdemServicoPorOrcamentoUseCase(ordemServicoGateway, RELOGIO);

        OrdemServico resultado = useCase.executar(5);

        assertEquals(StatusOrdemServico.FINALIZADA, resultado.getStatus());
        assertEquals(LocalDateTime.of(2026, 10, 16, 12, 0), resultado.getHistorico().get(0).getFim());
        assertEquals(StatusOrdemServico.FINALIZADA, resultado.getHistorico().get(1).getStatus());
    }

    @Test
    void rejeicaoSemOrdemDeveFalhar() {
        when(ordemServicoGateway.pesquisarPorOrcamentoId(5)).thenReturn(Optional.empty());
        var useCase = new FinalizarOrdemServicoPorOrcamentoUseCase(ordemServicoGateway, RELOGIO);

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(5));

        verify(ordemServicoGateway, never()).salvar(any());
    }
}
