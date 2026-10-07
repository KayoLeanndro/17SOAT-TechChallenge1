package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.ClienteJpaEntity;
import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import org.springframework.stereotype.Component;

@Component
public class ClienteJpaMapper {

    public Cliente paraDominio(ClienteJpaEntity e) {
        return Cliente.reconstruir(e.getId(), e.getNome(), new CpfCnpj(e.getCpfCnpj()),
                e.getTelefone(), e.getEmail(), e.getDataCriacao());
    }

    public ClienteJpaEntity paraEntity(Cliente cliente){

        var clienteJpaEntity = new ClienteJpaEntity(cliente.getNome(), cliente.getCpfCnpj().getDocumento(),cliente.getTelefone(), cliente.getEmail(), cliente.getDataCriacao());
        clienteJpaEntity.setId(cliente.getId());
        return clienteJpaEntity;
    }
}
