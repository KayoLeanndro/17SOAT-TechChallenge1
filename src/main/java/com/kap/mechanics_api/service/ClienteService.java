package com.kap.mechanics_api.service;

import org.springframework.stereotype.Service;

import com.kap.mechanics_api.adapter.persistence.entity.ClienteJpaEntity;
import com.kap.mechanics_api.core.cliente.usecase.ClienteNaoEncontradoException;
import com.kap.mechanics_api.repository.ClienteRepository;

/**
 * Resíduo da migração: o CRUD de cliente já vive nos casos de uso (core/cliente/usecase).
 * Resta apenas a consulta que VeiculoService e OrcamentoService ainda usam e que devolve
 * a entidade JPA. Remover quando esses dois contextos forem migrados.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteJpaEntity pesquisarPorId(Integer id){
        return clienteRepository.findById(id).orElseThrow( () -> new ClienteNaoEncontradoException(id));
    }
}
