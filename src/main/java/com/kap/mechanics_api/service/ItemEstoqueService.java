package com.kap.mechanics_api.service;

import org.springframework.stereotype.Service;

import com.kap.mechanics_api.adapter.persistence.entity.ItemEstoqueJpaEntity;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.repository.ItemEstoqueRepository;

/**
 * Resíduo da migração: o CRUD de item de estoque já vive nos casos de uso (core/itemestoque/usecase).
 * Resta apenas a consulta que OrcamentoItemService e OrdemServicoItemService ainda usam e que devolve
 * a entidade JPA. Remover quando Orçamento e Ordem de Serviço forem migrados.
 */
@Service
public class ItemEstoqueService {

    private final ItemEstoqueRepository itemEstoqueRepository;

    public ItemEstoqueService(ItemEstoqueRepository itemEstoqueRepository) {
        this.itemEstoqueRepository = itemEstoqueRepository;
    }

    public ItemEstoqueJpaEntity pesquisarPorId(Integer id) {
        return itemEstoqueRepository.findById(id)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(id));
    }
}
