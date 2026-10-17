package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.ItemEstoqueJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.MovimentacaoEstoqueJpaEntity;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import org.springframework.stereotype.Component;

@Component
public class MovimentacaoEstoqueJpaMapper {

    public MovimentacaoEstoque paraDominio(MovimentacaoEstoqueJpaEntity e) {
        return MovimentacaoEstoque.reconstruir(e.getId(), e.getItemEstoque().getId(), e.getTipo(), e.getQuantidade(),
                e.getDataHora(), e.getUsuario().getId(),
                e.getOrdemServico() == null ? null : e.getOrdemServico().getId());
    }

    // As associações chegam resolvidas pelo gateway: o domínio guarda só os ids.
    public MovimentacaoEstoqueJpaEntity paraEntity(MovimentacaoEstoque movimentacao, ItemEstoqueJpaEntity item,
                                                   Usuario usuario, OrdemServico ordemServico) {
        var entity = new MovimentacaoEstoqueJpaEntity(item, movimentacao.getTipo(), movimentacao.getQuantidade(),
                usuario, ordemServico);
        entity.setId(movimentacao.getId());
        entity.setDataHora(movimentacao.getDataHora());
        return entity;
    }
}
