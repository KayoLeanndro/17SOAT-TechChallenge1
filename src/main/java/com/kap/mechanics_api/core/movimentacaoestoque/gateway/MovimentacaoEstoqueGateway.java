package com.kap.mechanics_api.core.movimentacaoestoque.gateway;

import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.TipoMovimentacaoEstoque;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimentacaoEstoqueGateway {

    MovimentacaoEstoque salvar(MovimentacaoEstoque movimentacao);
    Optional<MovimentacaoEstoque> pesquisarPorId(Integer id);
    // As listagens abaixo vêm da mais recente para a mais antiga.
    List<MovimentacaoEstoque> listarTodas();
    List<MovimentacaoEstoque> listarPorItem(Integer itemEstoqueId);
    List<MovimentacaoEstoque> listarPorOrdemServico(Integer ordemServicoId);
    List<MovimentacaoEstoque> listarPorTipo(TipoMovimentacaoEstoque tipo);
    List<MovimentacaoEstoque> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
    // Histórico completo do item, da mais antiga para a mais recente (desempate pelo id).
    List<MovimentacaoEstoque> listarHistoricoDoItem(Integer itemEstoqueId);

}
