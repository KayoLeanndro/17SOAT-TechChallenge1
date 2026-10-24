package com.kap.mechanics_api.core.orcamento.usecase;

import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.orcamento.domain.ItemOrcamento;
import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;
import com.kap.mechanics_api.core.servico.usecase.ServicoNaoEncontradoException;

public class ConsultarItensDoOrcamentoUseCase {

    private final OrcamentoGateway orcamentoGateway;
    private final ServicoGateway servicoGateway;
    private final ItemEstoqueGateway itemEstoqueGateway;

    public ConsultarItensDoOrcamentoUseCase(OrcamentoGateway orcamentoGateway, ServicoGateway servicoGateway,
                                            ItemEstoqueGateway itemEstoqueGateway) {
        this.orcamentoGateway = orcamentoGateway;
        this.servicoGateway = servicoGateway;
        this.itemEstoqueGateway = itemEstoqueGateway;
    }

    public ConsultaDeItensDoOrcamento executar(Integer orcamentoId) {
        Orcamento orcamento = orcamentoGateway.pesquisarPorId(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"));

        var linhas = orcamento.getItens().stream().map(this::paraLinha).toList();
        return new ConsultaDeItensDoOrcamento(orcamentoId, orcamento.getStatus(), orcamento.getValorTotal(), linhas);
    }

    private ConsultaDeItensDoOrcamento.Linha paraLinha(ItemOrcamento item) {
        if (item.isServico()) {
            String nome = servicoGateway.pesquisarPorId(item.getServicoId())
                    .orElseThrow(() -> new ServicoNaoEncontradoException(item.getServicoId())).getNome();
            return new ConsultaDeItensDoOrcamento.Linha(item.getId(), ConsultaDeItensDoOrcamento.Tipo.SERVICO,
                    item.getServicoId(), nome, item.getQuantidade(), item.getValorUnitario(), item.valorTotal());
        }
        String nome = itemEstoqueGateway.pesquisarPorId(item.getItemEstoqueId())
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(item.getItemEstoqueId())).getNome();
        return new ConsultaDeItensDoOrcamento.Linha(item.getId(), ConsultaDeItensDoOrcamento.Tipo.ESTOQUE,
                item.getItemEstoqueId(), nome, item.getQuantidade(), item.getValorUnitario(), item.valorTotal());
    }
}
