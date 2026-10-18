package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.usecase.BaixarItemParaOrdemServicoUseCase;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;
import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ComponenteDeServico;
import com.kap.mechanics_api.core.servico.gateway.ComponentesDeServicoGateway;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;
import com.kap.mechanics_api.core.servico.usecase.ServicoNaoEncontradoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Inclui um serviço (com as peças padrão) ou uma peça avulsa numa ordem em execução, baixando do
 * estoque cada peça incluída.
 */
public class IncluirItemNaOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ServicoGateway servicoGateway;
    private final ItemEstoqueGateway itemEstoqueGateway;
    private final ComponentesDeServicoGateway componentesGateway;
    private final BaixarItemParaOrdemServicoUseCase baixarItem;

    public IncluirItemNaOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway, ServicoGateway servicoGateway,
                                            ItemEstoqueGateway itemEstoqueGateway,
                                            ComponentesDeServicoGateway componentesGateway,
                                            BaixarItemParaOrdemServicoUseCase baixarItem) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.servicoGateway = servicoGateway;
        this.itemEstoqueGateway = itemEstoqueGateway;
        this.componentesGateway = componentesGateway;
        this.baixarItem = baixarItem;
    }

    public void executar(Integer ordemServicoId, Integer servicoId, Integer itemEstoqueId, int quantidade) {
        OrdemServico ordemServico = ordemServicoGateway.pesquisarPorId(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));
        ordemServico.exigirEmExecucao();
        if ((servicoId == null) == (itemEstoqueId == null)) {
            throw new IllegalArgumentException("Informe exatamente um: servicoId ou itemEstoqueId.");
        }

        List<Baixa> baixas = new ArrayList<>();
        if (itemEstoqueId != null) {
            ItemEstoque item = buscarItem(itemEstoqueId);
            ordemServico.incluirItemEstoque(itemEstoqueId, quantidade, item.getValorUnitario());
            baixas.add(new Baixa(itemEstoqueId, quantidade));
        } else {
            Servico servico = servicoGateway.pesquisarPorId(servicoId)
                    .orElseThrow(() -> new ServicoNaoEncontradoException(servicoId));
            ordemServico.incluirServico(servicoId, quantidade, servico.getValorMaoDeObra());
            for (ComponenteDeServico componente : componentesGateway.listarPorServico(servicoId)) {
                int quantidadeTotal = Math.multiplyExact(quantidade, componente.quantidadePadrao());
                ItemEstoque item = buscarItem(componente.itemEstoqueId());
                ordemServico.incluirItemEstoque(componente.itemEstoqueId(), quantidadeTotal, item.getValorUnitario());
                baixas.add(new Baixa(componente.itemEstoqueId(), quantidadeTotal));
            }
        }

        ordemServicoGateway.salvar(ordemServico);
        for (Baixa baixa : baixas) {
            baixarItem.executar(ordemServicoId, baixa.itemEstoqueId(), baixa.quantidade());
        }
    }

    private ItemEstoque buscarItem(Integer itemEstoqueId) {
        return itemEstoqueGateway.pesquisarPorId(itemEstoqueId)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(itemEstoqueId));
    }

    private record Baixa(Integer itemEstoqueId, int quantidade) {
    }
}
