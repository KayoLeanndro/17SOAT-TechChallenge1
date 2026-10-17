package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoParaEstoque;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEstaEmExecucaoException;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Baixa do estoque, em nome do atendente da ordem, a quantidade de um item usada na ordem de serviço.
 */
public class BaixarItemParaOrdemServicoUseCase {

    private final ItemEstoqueGateway itemGateway;
    private final MovimentacaoEstoqueGateway movimentacaoGateway;
    private final OrdemServicoEstoqueGateway ordemServicoGateway;
    private final Clock clock;

    public BaixarItemParaOrdemServicoUseCase(ItemEstoqueGateway itemGateway,
                                             MovimentacaoEstoqueGateway movimentacaoGateway,
                                             OrdemServicoEstoqueGateway ordemServicoGateway, Clock clock) {
        this.itemGateway = itemGateway;
        this.movimentacaoGateway = movimentacaoGateway;
        this.ordemServicoGateway = ordemServicoGateway;
        this.clock = clock;
    }

    public void executar(Integer ordemServicoId, Integer itemEstoqueId, Integer quantidade) {
        OrdemServicoParaEstoque ordemServico = ordemServicoGateway.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));
        if (!ordemServico.emExecucao()) {
            throw new OrdemServicoNaoEstaEmExecucaoException(ordemServicoId);
        }

        ItemEstoque item = itemGateway.pesquisarPorIdParaAtualizacao(itemEstoqueId)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(itemEstoqueId));

        item.registrarSaida(quantidade);
        itemGateway.salvar(item);
        movimentacaoGateway.salvar(MovimentacaoEstoque.saida(
                itemEstoqueId, quantidade, ordemServico.usuarioAtendenteId(), ordemServicoId,
                LocalDateTime.now(clock)));
    }
}
