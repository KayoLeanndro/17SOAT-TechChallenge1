package com.kap.mechanics_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "orcamento_id", nullable = false, unique = true)
    private Orcamento orcamento;

    @ManyToOne
    @JoinColumn(name = "usuario_atendente_id", nullable = false)
    private Usuario usuarioAtendente;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusOrdemServico status;

    @Column(name = "data_abertura", nullable = false, updatable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @PrePersist
    private void prePersist() {
        if (dataAbertura == null) {
            dataAbertura = LocalDateTime.now();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public Usuario getUsuarioAtendente() {
        return usuarioAtendente;
    }

    public void setUsuarioAtendente(Usuario usuarioAtendente) {
        this.usuarioAtendente = usuarioAtendente;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDateTime dataEntrega) {
        this.dataEntrega = dataEntrega;
    }
}
