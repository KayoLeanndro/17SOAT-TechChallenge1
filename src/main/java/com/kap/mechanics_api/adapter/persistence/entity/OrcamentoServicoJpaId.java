package com.kap.mechanics_api.adapter.persistence.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrcamentoServicoJpaId implements Serializable {

    private Integer orcamentoId;
    private Integer servicoId;

    public OrcamentoServicoJpaId(){}

    public OrcamentoServicoJpaId(Integer orcamentoId, Integer servicoId) {
        this.orcamentoId = orcamentoId;
        this.servicoId = servicoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrcamentoServicoJpaId)) return false;
        OrcamentoServicoJpaId that = (OrcamentoServicoJpaId) o;
        return Objects.equals(servicoId, that.servicoId) &&
                Objects.equals(orcamentoId, that.orcamentoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servicoId, orcamentoId);
    }

    public Integer getOrcamentoId() {
        return orcamentoId;
    }

    public void setOrcamentoId(Integer orcamentoId) {
        this.orcamentoId = orcamentoId;
    }

    public Integer getServicoId() {
        return servicoId;
    }

    public void setServicoId(Integer servicoId) {
        this.servicoId = servicoId;
    }
}
