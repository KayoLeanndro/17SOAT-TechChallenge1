package com.kap.mechanics_api.core.movimentacaoestoque.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoqueConsulta;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.MovimentacaoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.ResponsavelMovimentacaoGateway;

import java.time.Clock;
import java.time.LocalDateTime;

public class RegistrarEntradaEstoqueUseCase {

    private final ItemEstoqueGateway itemGateway;
    private final MovimentacaoEstoqueGateway movimentacaoGateway;
    private final ResponsavelMovimentacaoGateway responsavelGateway;
    private final Clock clock;

    public RegistrarEntradaEstoqueUseCase(ItemEstoqueGateway itemGateway,
                                          MovimentacaoEstoqueGateway movimentacaoGateway,
                                          ResponsavelMovimentacaoGateway responsavelGateway, Clock clock) {
        this.itemGateway = itemGateway;
        this.movimentacaoGateway = movimentacaoGateway;
        this.responsavelGateway = responsavelGateway;
        this.clock = clock;
    }

    public MovimentacaoEstoqueConsulta executar(Integer itemEstoqueId, Integer quantidade, String usuarioLogin) {
        ItemEstoque item = itemGateway.pesquisarPorIdParaAtualizacao(itemEstoqueId)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(itemEstoqueId));
        Integer usuarioId = responsavelGateway.buscarIdPorLogin(usuarioLogin)
                .orElseThrow(() -> new ResponsavelNaoEncontradoException(usuarioLogin));

        item.registrarEntrada(quantidade);
        itemGateway.salvar(item);
        MovimentacaoEstoque movimentacao = movimentacaoGateway.salvar(
                MovimentacaoEstoque.entrada(itemEstoqueId, quantidade, usuarioId, LocalDateTime.now(clock)));

        return new MovimentacaoEstoqueConsulta(movimentacao, item.getNome(), item.getQuantidadeAtual());
    }
}
