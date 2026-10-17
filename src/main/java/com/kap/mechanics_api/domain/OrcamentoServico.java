package com.kap.mechanics_api.domain;

import com.kap.mechanics_api.adapter.persistence.entity.ServicoJpaEntity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orcamento_servico")
public class OrcamentoServico {

    @EmbeddedId
    private OrcamentoServicoId id;

    @ManyToOne
    @MapsId("orcamentoId")
    @JoinColumn(name = "orcamento_id")
    private Orcamento orcamento;

    @ManyToOne
    @MapsId("servicoId")
    @JoinColumn(name = "servico_id")
    private ServicoJpaEntity servico;

    @Column(name = "valor_cobrado")
    private BigDecimal valorCobrado;

    public OrcamentoServico(){}

    public OrcamentoServico(Orcamento orcamento, ServicoJpaEntity servico, BigDecimal valorCobrado) {
        this.orcamento = orcamento;
        this.servico = servico;
        this.valorCobrado = valorCobrado;
    }

    public OrcamentoServicoId getId() {
        return id;
    }

    public void setId(OrcamentoServicoId id) {
        this.id = id;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
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
