package com.kap.mechanics_api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "servico_peca")
public class ServicoPeca {

    @EmbeddedId
    private ServicoPecaId id;

    @ManyToOne
    @MapsId("servicoId")
    @JoinColumn(name = "servico_id")
    private Servico servico;

    @ManyToOne
    @MapsId("pecaId")
    @JoinColumn(name = "peca_id")
    private Peca peca;

    private Integer quantidadePadrao;

    public ServicoPeca() {}

    public ServicoPeca(Servico servico, Peca peca, Integer quantidadePadrao) {
        this.servico = servico;
        this.peca = peca;
        this.id = new ServicoPecaId(servico.getId(), peca.getId());
        this.quantidadePadrao = quantidadePadrao;
    }

    public ServicoPecaId getId() {
        return id;
    }

    public void setId(ServicoPecaId id) {
        this.id = id;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public Integer getQuantidadePadrao() {
        return quantidadePadrao;
    }

    public void setQuantidadePadrao(Integer quantidadePadrao) {
        this.quantidadePadrao = quantidadePadrao;
    }
}
