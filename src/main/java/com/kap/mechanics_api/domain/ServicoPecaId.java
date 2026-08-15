package com.kap.mechanics_api.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ServicoPecaId implements Serializable {

    private Integer servicoId;
    private Integer pecaId;

    public ServicoPecaId() {}

    public ServicoPecaId(Integer servicoId, Integer pecaId) {
        this.servicoId = servicoId;
        this.pecaId = pecaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServicoPecaId)) return false;
        ServicoPecaId that = (ServicoPecaId) o;
        return Objects.equals(servicoId, that.servicoId) &&
                Objects.equals(pecaId, that.pecaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servicoId, pecaId);
    }

    public Integer getServicoId() {
        return servicoId;
    }

    public void setServicoId(Integer servicoId) {
        this.servicoId = servicoId;
    }

    public Integer getPecaId() {
        return pecaId;
    }

    public void setPecaId(Integer pecaId) {
        this.pecaId = pecaId;
    }
}
