package com.kap.mechanics_api.service;

import org.springframework.stereotype.Service;

import com.kap.mechanics_api.adapter.persistence.entity.VeiculoJpaEntity;
import com.kap.mechanics_api.core.veiculo.usecase.VeiculoNaoEncontradoException;
import com.kap.mechanics_api.repository.VeiculoRepository;

/**
 * Resíduo da migração: o CRUD de veículo já vive nos casos de uso (core/veiculo/usecase).
 * Resta apenas a consulta que OrcamentoService ainda usa e que devolve a entidade JPA.
 * Remover quando o contexto Orçamento for migrado.
 */
@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public VeiculoJpaEntity pesquisarPorId(Integer id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException(id));
    }
}
