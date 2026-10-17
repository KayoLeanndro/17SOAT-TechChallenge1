package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.ItemEstoqueJpaEntity;
import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import org.springframework.stereotype.Component;

@Component
public class ItemEstoqueJpaMapper {

    public ItemEstoque paraDominio(ItemEstoqueJpaEntity e) {
        return ItemEstoque.reconstruir(e.getId(), e.getNome(), e.getDescricao(), e.getTipoItemEstoque(),
                e.getValorUnitario(), e.getQuantidadeAtual(), e.getQuantidadeMinima(), e.isAtivo());
    }

    public ItemEstoqueJpaEntity paraEntity(ItemEstoque item) {
        var entity = new ItemEstoqueJpaEntity(item.getNome(), item.getDescricao(), item.getTipoItemEstoque(),
                item.getValorUnitario(), item.getQuantidadeAtual(), item.getQuantidadeMinima(), item.isAtivo());
        entity.setId(item.getId());
        return entity;
    }
}
