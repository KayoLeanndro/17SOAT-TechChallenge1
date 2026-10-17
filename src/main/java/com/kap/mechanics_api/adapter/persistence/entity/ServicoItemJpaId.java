package com.kap.mechanics_api.adapter.persistence.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ServicoItemJpaId implements Serializable {

    private Integer servicoId;
    private Integer itemEstoqueId;

    public ServicoItemJpaId() {
    }

    public ServicoItemJpaId(Integer servicoId, Integer itemEstoqueId) {
        this.servicoId = servicoId;
        this.itemEstoqueId = itemEstoqueId;
    }

    public Integer getServicoId() {
        return servicoId;
    }

    public void setServicoId(Integer servicoId) {
        this.servicoId = servicoId;
    }

    public Integer getItemEstoqueId() {
        return itemEstoqueId;
    }

    public void setItemEstoqueId(Integer itemEstoqueId) {
        this.itemEstoqueId = itemEstoqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServicoItemJpaId)) return false;
        ServicoItemJpaId that = (ServicoItemJpaId) o;
        return Objects.equals(servicoId, that.servicoId) &&
                Objects.equals(itemEstoqueId, that.itemEstoqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servicoId, itemEstoqueId);
    }
}