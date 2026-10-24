package com.kap.mechanics_api.core.ordemservico.usecase;

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
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.orcamento.usecase.OrcamentoNaoEncontradoException;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.StatusOrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.AtendenteGateway;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

// Cobre os dois jeitos de abrir uma ordem (para orçamento pendente e para orçamento aprovado).
@ExtendWith(MockitoExtension.class)
class AbrirOrdemServicoUseCasesTest {

    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-10-16T12:00:00Z"), ZoneOffset.UTC);

    @Mock private OrdemServicoGateway ordemServicoGateway;
    @Mock private OrcamentoGateway orcamentoGateway;
    @Mock private AtendenteGateway atendenteGateway;

    private CriarOrdemServicoParaOrcamentoPendenteUseCase criarParaPendente;
    private GerarOrdemServicoUseCase gerarParaAprovado;

    @BeforeEach
    void montar() {
        var abridor = new AbridorDeOrdemServico(ordemServicoGateway, atendenteGateway, RELOGIO);
        criarParaPendente = new CriarOrdemServicoParaOrcamentoPendenteUseCase(orcamentoGateway, abridor);
        gerarParaAprovado = new GerarOrdemServicoUseCase(orcamentoGateway, abridor);
    }

    private void orcamento(StatusOrcamento status) {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(Orcamento.reconstruir(5, 1, 2,
                BigDecimal.TEN, status, LocalDateTime.now(), null, List.of(), List.of())));
    }

    private void atendenteExiste() {
        when(atendenteGateway.buscarIdPorLogin("maria")).thenReturn(Optional.of(3));
    }

    @Test
    void deveCriarOrdemInicialAguardandoAprovacaoParaOrcamentoPendente() {
        atendenteExiste();
        orcamento(StatusOrcamento.PENDENTE);
        when(ordemServicoGateway.existePorOrcamentoId(5)).thenReturn(false);
        when(ordemServicoGateway.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico os = criarParaPendente.executar(5, "maria");

        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, os.getStatus());
        assertEquals(5, os.getOrcamentoId());
        assertEquals(3, os.getAtendenteId());
        assertEquals(LocalDateTime.of(2026, 10, 16, 12, 0), os.getDataAbertura());
        assertEquals(1, os.getHistorico().size());
    }

    @Test
    void naoDeveCriarOrdemInicialParaOrcamentoJaRespondido() {
        atendenteExiste();
        orcamento(StatusOrcamento.APROVADO);

        assertThrows(IllegalArgumentException.class, () -> criarParaPendente.executar(5, "maria"));

        verify(ordemServicoGateway, never()).salvar(any());
    }

    @Test
    void deveGerarOrdemParaOrcamentoAprovadoAssociandoOAtendenteLogado() {
        atendenteExiste();
        orcamento(StatusOrcamento.APROVADO);
        when(ordemServicoGateway.existePorOrcamentoId(5)).thenReturn(false);
        when(ordemServicoGateway.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        gerarParaAprovado.executar(5, "maria");

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);
        verify(ordemServicoGateway).salvar(captor.capture());
        assertEquals(3, captor.getValue().getAtendenteId());
    }

    @Test
    void naoDeveGerarOrdemQuandoOrcamentoNaoEstaAprovado() {
        atendenteExiste();
        orcamento(StatusOrcamento.PENDENTE);

        assertThrows(OrcamentoNaoAprovadoException.class, () -> gerarParaAprovado.executar(5, "maria"));

        verify(ordemServicoGateway, never()).salvar(any());
    }

    @Test
    void naoDeveAbrirSegundaOrdemParaOMesmoOrcamento() {
        atendenteExiste();
        orcamento(StatusOrcamento.APROVADO);
        when(ordemServicoGateway.existePorOrcamentoId(5)).thenReturn(true);

        assertThrows(OrdemServicoJaExisteException.class, () -> gerarParaAprovado.executar(5, "maria"));

        verify(ordemServicoGateway, never()).salvar(any());
    }

    @Test
    void deveFalharQuandoOAtendenteNaoExiste() {
        when(atendenteGateway.buscarIdPorLogin("fantasma")).thenReturn(Optional.empty());

        assertThrows(AtendenteNaoEncontradoException.class, () -> gerarParaAprovado.executar(5, "fantasma"));
        assertThrows(AtendenteNaoEncontradoException.class, () -> criarParaPendente.executar(5, "fantasma"));

        verifyNoInteractions(ordemServicoGateway);
    }

    @Test
    void deveFalharQuandoOOrcamentoNaoExiste() {
        atendenteExiste();
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> gerarParaAprovado.executar(5, "maria"));
        assertThrows(OrcamentoNaoEncontradoException.class, () -> criarParaPendente.executar(5, "maria"));
    }
}
