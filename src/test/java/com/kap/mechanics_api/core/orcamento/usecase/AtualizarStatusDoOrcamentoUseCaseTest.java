package com.kap.mechanics_api.core.orcamento.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;
import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamentoInvalidoException;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.ordemservico.usecase.FinalizarOrdemServicoPorOrcamentoUseCase;
import com.kap.mechanics_api.core.ordemservico.usecase.IniciarDiagnosticoPorOrcamentoUseCase;

@ExtendWith(MockitoExtension.class)
class AtualizarStatusDoOrcamentoUseCaseTest {

    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-10-16T12:00:00Z"), ZoneOffset.UTC);

    @Mock private OrcamentoGateway orcamentoGateway;
    @Mock private IniciarDiagnosticoPorOrcamentoUseCase iniciarDiagnostico;
    @Mock private FinalizarOrdemServicoPorOrcamentoUseCase finalizarOrdemServico;

    private AtualizarStatusDoOrcamentoUseCase useCase;

    @BeforeEach
    void montar() {
        useCase = new AtualizarStatusDoOrcamentoUseCase(orcamentoGateway, iniciarDiagnostico, finalizarOrdemServico,
                RELOGIO);
    }

    private void orcamentoPendente() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(Orcamento.reconstruir(5, 1, 2,
                BigDecimal.TEN, StatusOrcamento.PENDENTE, LocalDateTime.now(), null, List.of(), List.of())));
    }

    @Test
    void aprovarDeveGravarDataDeRespostaEIniciarODiagnosticoDaOrdem() {
        orcamentoPendente();

        useCase.executar(5, "aprovado");

        ArgumentCaptor<Orcamento> captor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoGateway).salvar(captor.capture());
        assertEquals(StatusOrcamento.APROVADO, captor.getValue().getStatus());
        assertEquals(LocalDateTime.of(2026, 10, 16, 12, 0), captor.getValue().getDataResposta());
        verify(iniciarDiagnostico).executar(5);
        verifyNoInteractions(finalizarOrdemServico);
    }

    @Test
    void rejeitarDeveFinalizarAOrdem() {
        orcamentoPendente();

        useCase.executar(5, "REJEITADO");

        verify(finalizarOrdemServico).executar(5);
        verifyNoInteractions(iniciarDiagnostico);
    }

    @Test
    void voltarParaPendenteNaoDeveMexerNaOrdem() {
        orcamentoPendente();

        useCase.executar(5, "PENDENTE");

        verify(orcamentoGateway).salvar(any());
        verifyNoInteractions(iniciarDiagnostico, finalizarOrdemServico);
    }

    @Test
    void deveFalharQuandoOrcamentoNaoExiste() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> useCase.executar(5, "APROVADO"));

        verify(orcamentoGateway, never()).salvar(any());
    }

    @Test
    void deveFalharComStatusInvalidoSemGravar() {
        orcamentoPendente();

        assertThrows(StatusOrcamentoInvalidoException.class, () -> useCase.executar(5, "TALVEZ"));

        verify(orcamentoGateway, never()).salvar(any());
        verifyNoInteractions(iniciarDiagnostico, finalizarOrdemServico);
    }
}
