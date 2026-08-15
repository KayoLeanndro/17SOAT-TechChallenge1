package com.kap.mechanics_api.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrcamentoServicoId implements Serializable {

    private Integer orcamentoId;
    private Integer servicoId;

    public OrcamentoServicoId(){}

    public OrcamentoServicoId(Integer orcamentoId, Integer servicoId) {
        this.orcamentoId = orcamentoId;
        this.servicoId = servicoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServicoPecaId)) return false;
        OrcamentoServicoId that = (OrcamentoServicoId) o;
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
