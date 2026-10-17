package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoParaEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.QuantidadeDeItem;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEstaEmExecucaoException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Baixa do estoque tudo o que a ordem consome quando entra em execução. Usa as linhas da própria
 * ordem; se ela não tiver nenhuma, cai para as peças padrão dos serviços do orçamento.
 */
public class BaixarItensDaOrdemServicoUseCase {

    private final OrdemServicoEstoqueGateway ordemServicoGateway;
    private final BaixarItemParaOrdemServicoUseCase baixarItem;

    public BaixarItensDaOrdemServicoUseCase(OrdemServicoEstoqueGateway ordemServicoGateway,
                                            BaixarItemParaOrdemServicoUseCase baixarItem) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.baixarItem = baixarItem;
    }

    public void executar(Integer ordemServicoId) {
        OrdemServicoParaEstoque ordemServico = ordemServicoGateway.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));
        if (!ordemServico.emExecucao()) {
            throw new OrdemServicoNaoEstaEmExecucaoException(ordemServicoId);
        }

        List<QuantidadeDeItem> linhas = ordemServicoGateway.listarLinhasDaOrdem(ordemServicoId);
        if (!linhas.isEmpty()) {
            linhas.stream()
                    .filter(linha -> linha.itemEstoqueId() != null)
                    .forEach(linha -> baixarItem.executar(ordemServicoId, linha.itemEstoqueId(), linha.quantidade()));
            return;
        }

        Map<Integer, Integer> quantidadesPorItem = new LinkedHashMap<>();
        for (QuantidadeDeItem componente : ordemServicoGateway.listarComponentesDosServicosOrcados(ordemServicoId)) {
            quantidadesPorItem.merge(componente.itemEstoqueId(), componente.quantidade(), Math::addExact);
        }
        quantidadesPorItem.forEach((itemId, quantidade) -> baixarItem.executar(ordemServicoId, itemId, quantidade));
    }
}
