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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.movimentacaoestoque.usecase.BaixarItensDaOrdemServicoUseCase;
import com.kap.mechanics_api.core.orcamento.domain.ItemOrcamento;
import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.ordemservico.domain.HistoricoDeStatus;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.StatusOrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.TransicaoStatusInvalidaException;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

@ExtendWith(MockitoExtension.class)
class TransicionarStatusOrdemServicoUseCaseTest {

    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-10-16T12:00:00Z"), ZoneOffset.UTC);
    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 10, 16, 9, 0);

    @Mock private OrdemServicoGateway ordemServicoGateway;
    @Mock private OrcamentoGateway orcamentoGateway;
    @Mock private BaixarItensDaOrdemServicoUseCase baixarItens;

    private TransicionarStatusOrdemServicoUseCase useCase;

    @BeforeEach
    void montar() {
        useCase = new TransicionarStatusOrdemServicoUseCase(ordemServicoGateway, orcamentoGateway, baixarItens,
                RELOGIO);
    }

    private OrdemServico ordemNoStatus(StatusOrdemServico status) {
        return OrdemServico.reconstruir(9, 5, 3, status, ABERTURA, null,
                List.of(HistoricoDeStatus.reconstruir(1L, status, ABERTURA, null)), List.of());
    }

    @Test
    void deveAvancarOStatusEGravarSemBaixarEstoque() {
        when(ordemServicoGateway.pesquisarPorId(9))
                .thenReturn(Optional.of(ordemNoStatus(StatusOrdemServico.AGUARDANDO_APROVACAO)));
        when(ordemServicoGateway.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resultado = useCase.executar(9, StatusOrdemServico.EM_DIAGNOSTICO);

        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, resultado.getStatus());
        assertEquals(LocalDateTime.of(2026, 10, 16, 12, 0), resultado.getHistorico().getFirst().getFim());
        verifyNoInteractions(baixarItens, orcamentoGateway);
    }

    @Test
    void aoEntrarEmExecucaoDeveCopiarOsItensDoOrcamentoGravarEBaixarOEstoque() {
        when(ordemServicoGateway.pesquisarPorId(9))
                .thenReturn(Optional.of(ordemNoStatus(StatusOrdemServico.EM_DIAGNOSTICO)));
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(Orcamento.reconstruir(5, 1, 2,
                BigDecimal.TEN, StatusOrcamento.APROVADO, ABERTURA, null,
                List.of(ItemOrcamento.reconstruir(21, 10, null, 1, new BigDecimal("100.00")),
                        ItemOrcamento.reconstruir(22, null, 7, 2, new BigDecimal("30.00"))),
                List.of())));
        when(ordemServicoGateway.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.executar(9, StatusOrdemServico.EM_EXECUCAO);

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);
        InOrder ordem = inOrder(ordemServicoGateway, baixarItens);
        ordem.verify(ordemServicoGateway).salvar(captor.capture());
        ordem.verify(baixarItens).executar(9); // a baixa só acontece depois de gravar o novo status
        assertEquals(2, captor.getValue().getItens().size());
        assertEquals(21, captor.getValue().getItens().get(0).getOrcamentoItemId());
        assertEquals(7, captor.getValue().getItens().get(1).getItemEstoqueId());
    }

    @Test
    void transicaoInvalidaNaoDeveGravarNemBaixarEstoque() {
        when(ordemServicoGateway.pesquisarPorId(9))
                .thenReturn(Optional.of(ordemNoStatus(StatusOrdemServico.RECEBIDA)));

        assertThrows(TransicaoStatusInvalidaException.class,
                () -> useCase.executar(9, StatusOrdemServico.EM_EXECUCAO));

        verify(ordemServicoGateway, never()).salvar(any());
        verifyNoInteractions(baixarItens);
    }

    @Test
    void deveFalharQuandoAOrdemNaoExiste() {
        when(ordemServicoGateway.pesquisarPorId(9)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class,
                () -> useCase.executar(9, StatusOrdemServico.EM_DIAGNOSTICO));
    }
}
