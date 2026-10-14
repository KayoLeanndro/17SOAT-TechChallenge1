package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.VeiculoJpaEntity;
import com.kap.mechanics_api.core.veiculo.domain.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class VeiculoJpaMapper {

    public Veiculo paraDominio(VeiculoJpaEntity e) {
        return Veiculo.reconstruir(e.getId(), e.getPlaca(), e.getMarca(), e.getModelo(), e.getAno(), e.getDataCriacao());
    }

    public VeiculoJpaEntity paraEntity(Veiculo veiculo) {
        var entity = new VeiculoJpaEntity(veiculo.getId(), veiculo.getPlaca(), veiculo.getMarca(), veiculo.getModelo(), veiculo.getAno());
        entity.setDataCriacao(veiculo.getDataCriacao());
        return entity;
    }
}
