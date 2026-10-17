package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.ServicoJpaEntity;
import com.kap.mechanics_api.core.servico.domain.Servico;
import org.springframework.stereotype.Component;

@Component
public class ServicoJpaMapper {

    public Servico paraDominio(ServicoJpaEntity e) {
        return Servico.reconstruir(e.getId(), e.getNome(), e.getDescricao(), e.getValorMaoDeObra(),
                e.getTempoEstimadoMin(), e.isAtivo());
    }

    public ServicoJpaEntity paraEntity(Servico servico) {
        var entity = new ServicoJpaEntity(servico.getNome(), servico.getDescricao(), servico.getValorMaoDeObra(),
                servico.getTempoEstimadoMin(), servico.isAtivo());
        entity.setId(servico.getId());
        return entity;
    }
}
