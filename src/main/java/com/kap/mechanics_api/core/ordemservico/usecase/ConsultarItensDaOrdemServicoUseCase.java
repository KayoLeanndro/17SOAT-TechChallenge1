package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.ordemservico.domain.ItemOrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;
import com.kap.mechanics_api.core.servico.usecase.ServicoNaoEncontradoException;

import java.math.BigDecimal;
import java.util.List;

public class ConsultarItensDaOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ServicoGateway servicoGateway;
    private final ItemEstoqueGateway itemEstoqueGateway;

    public ConsultarItensDaOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway,
                                               ServicoGateway servicoGateway, ItemEstoqueGateway itemEstoqueGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.servicoGateway = servicoGateway;
        this.itemEstoqueGateway = itemEstoqueGateway;
    }

    public ConsultaDeItensDaOrdemServico executar(Integer ordemServicoId) {
        OrdemServico ordemServico = ordemServicoGateway.pesquisarPorId(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));

        List<ConsultaDeItensDaOrdemServico.Linha> linhas = ordemServico.getItens().stream()
                .map(this::paraLinha)
                .toList();
        BigDecimal total = linhas.stream()
                .map(ConsultaDeItensDaOrdemServico.Linha::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ConsultaDeItensDaOrdemServico(ordemServicoId, ordemServico.getOrcamentoId(),
                ordemServico.getStatus(), total, linhas);
    }

    private ConsultaDeItensDaOrdemServico.Linha paraLinha(ItemOrdemServico item) {
        if (item.getServicoId() != null) {
            String nome = servicoGateway.pesquisarPorId(item.getServicoId())
                    .orElseThrow(() -> new ServicoNaoEncontradoException(item.getServicoId())).getNome();
            return new ConsultaDeItensDaOrdemServico.Linha(item.getId(), item.getOrcamentoItemId(),
                    ConsultaDeItensDaOrdemServico.Tipo.SERVICO, item.getServicoId(), nome, item.getQuantidade(),
                    item.getValorUnitario(), item.valorTotal());
        }
        String nome = itemEstoqueGateway.pesquisarPorId(item.getItemEstoqueId())
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(item.getItemEstoqueId())).getNome();
        return new ConsultaDeItensDaOrdemServico.Linha(item.getId(), item.getOrcamentoItemId(),
                ConsultaDeItensDaOrdemServico.Tipo.ESTOQUE, item.getItemEstoqueId(), nome, item.getQuantidade(),
                item.getValorUnitario(), item.valorTotal());
    }
}
