package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.movimentacaoestoque.usecase.BaixarItensDaOrdemServicoUseCase;
import com.kap.mechanics_api.core.orcamento.domain.ItemOrcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.orcamento.usecase.OrcamentoNaoEncontradoException;
import com.kap.mechanics_api.core.ordemservico.domain.ItemOrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.StatusOrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Avança a ordem no fluxo de status. Ao entrar em execução, copia as linhas do orçamento e baixa do
 * estoque tudo o que a ordem consome.
 */
public class TransicionarStatusOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final OrcamentoGateway orcamentoGateway;
    private final BaixarItensDaOrdemServicoUseCase baixarItens;
    private final Clock clock;

    public TransicionarStatusOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway,
                                                 OrcamentoGateway orcamentoGateway,
                                                 BaixarItensDaOrdemServicoUseCase baixarItens, Clock clock) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.orcamentoGateway = orcamentoGateway;
        this.baixarItens = baixarItens;
        this.clock = clock;
    }

    public OrdemServico executar(Integer ordemServicoId, StatusOrdemServico novoStatus) {
        OrdemServico ordemServico = ordemServicoGateway.pesquisarPorId(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));

        ordemServico.transicionarPara(novoStatus, LocalDateTime.now(clock));

        if (novoStatus != StatusOrdemServico.EM_EXECUCAO) {
            return ordemServicoGateway.salvar(ordemServico);
        }

        ordemServico.copiarItensDoOrcamento(itensDoOrcamento(ordemServico.getOrcamentoId()));
        // a baixa de estoque lê a ordem pelo gateway, então o novo status e as linhas precisam estar gravados antes
        OrdemServico gravada = ordemServicoGateway.salvar(ordemServico);
        baixarItens.executar(gravada.getId());
        return gravada;
    }

    private List<ItemOrdemServico> itensDoOrcamento(Integer orcamentoId) {
        return orcamentoGateway.pesquisarPorId(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"))
                .getItens().stream()
                .map(this::copiar)
                .toList();
    }

    private ItemOrdemServico copiar(ItemOrcamento origem) {
        return ItemOrdemServico.novo(origem.getId(), origem.getServicoId(), origem.getItemEstoqueId(),
                origem.getQuantidade(), origem.getValorUnitario());
    }
}
