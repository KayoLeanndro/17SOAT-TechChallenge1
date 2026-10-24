package com.kap.mechanics_api.adapter.persistence.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orcamento_servico")
public class OrcamentoServicoJpaEntity {

    @EmbeddedId
    private OrcamentoServicoJpaId id;

    @ManyToOne
    @MapsId("orcamentoId")
    @JoinColumn(name = "orcamento_id")
    private OrcamentoJpaEntity orcamento;

    @ManyToOne
    @MapsId("servicoId")
    @JoinColumn(name = "servico_id")
    private ServicoJpaEntity servico;

    @Column(name = "valor_cobrado")
    private BigDecimal valorCobrado;

    public OrcamentoServicoJpaEntity(){}

    public OrcamentoServicoJpaEntity(OrcamentoJpaEntity orcamento, ServicoJpaEntity servico, BigDecimal valorCobrado) {
        this.orcamento = orcamento;
        this.servico = servico;
        this.valorCobrado = valorCobrado;
    }

    public OrcamentoServicoJpaId getId() {
        return id;
    }

    public void setId(OrcamentoServicoJpaId id) {
        this.id = id;
    }

    public OrcamentoJpaEntity getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(OrcamentoJpaEntity orcamento) {
        this.orcamento = orcamento;
    }

    public ServicoJpaEntity getServico() {
        return servico;
    }

    public void setServico(ServicoJpaEntity servico) {
        this.servico = servico;
    }


    public BigDecimal getValorCobrado() {
        return valorCobrado;
    }

    public void setValorCobrado(BigDecimal valorCobrado) {
        this.valorCobrado = valorCobrado;
    }
}
