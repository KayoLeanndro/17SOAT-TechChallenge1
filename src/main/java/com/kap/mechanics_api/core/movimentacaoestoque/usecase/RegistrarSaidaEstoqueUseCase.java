package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoParaEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.ResponsavelMovimentacaoGateway;
import com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServicoNaoEstaEmExecucaoException;

import java.time.Clock;
import java.time.LocalDateTime;

public class RegistrarSaidaEstoqueUseCase {

    private final ItemEstoqueGateway itemGateway;
    private final MovimentacaoEstoqueGateway movimentacaoGateway;
    private final ResponsavelMovimentacaoGateway responsavelGateway;
    private final OrdemServicoEstoqueGateway ordemServicoGateway;
    private final Clock clock;

    public RegistrarSaidaEstoqueUseCase(ItemEstoqueGateway itemGateway,
                                        MovimentacaoEstoqueGateway movimentacaoGateway,
                                        ResponsavelMovimentacaoGateway responsavelGateway,
                                        OrdemServicoEstoqueGateway ordemServicoGateway, Clock clock) {
        this.itemGateway = itemGateway;
        this.movimentacaoGateway = movimentacaoGateway;
        this.responsavelGateway = responsavelGateway;
        this.ordemServicoGateway = ordemServicoGateway;
        this.clock = clock;
    }

    public MovimentacaoEstoqueConsulta executar(Integer itemEstoqueId, Integer quantidade, Integer ordemServicoId,
                                                String usuarioLogin) {
        ItemEstoque item = itemGateway.pesquisarPorIdParaAtualizacao(itemEstoqueId)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(itemEstoqueId));
        Integer usuarioId = responsavelGateway.buscarIdPorLogin(usuarioLogin)
                .orElseThrow(() -> new ResponsavelNaoEncontradoException(usuarioLogin));
        OrdemServicoParaEstoque ordemServico = ordemServicoGateway.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));
        if (!ordemServico.emExecucao()) {
            throw new OrdemServicoNaoEstaEmExecucaoException(ordemServicoId);
        }

        item.registrarSaida(quantidade);
        itemGateway.salvar(item);
        MovimentacaoEstoque movimentacao = movimentacaoGateway.salvar(
                MovimentacaoEstoque.saida(itemEstoqueId, quantidade, usuarioId, ordemServicoId,
                        LocalDateTime.now(clock)));

        return new MovimentacaoEstoqueConsulta(movimentacao, item.getNome(), item.getQuantidadeAtual());
    }
}
