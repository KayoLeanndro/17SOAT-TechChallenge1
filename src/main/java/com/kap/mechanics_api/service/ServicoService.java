package com.kap.mechanics_api.service;

import org.springframework.stereotype.Service;

import com.kap.mechanics_api.adapter.persistence.entity.ServicoJpaEntity;
import com.kap.mechanics_api.core.servico.usecase.ServicoNaoEncontradoException;
import com.kap.mechanics_api.repository.ServicoRepository;

/**
 * Resíduo da migração: o CRUD de serviço já vive nos casos de uso (core/servico/usecase).
 * Resta apenas a consulta que OrcamentoService, OrcamentoItemService e OrdemServicoItemService
 * ainda usam e que devolve a entidade JPA. Remover quando Orçamento e Ordem de Serviço forem migrados.
 */
@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public ServicoJpaEntity pesquisarPorId(Integer id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException(id));
    }
}
