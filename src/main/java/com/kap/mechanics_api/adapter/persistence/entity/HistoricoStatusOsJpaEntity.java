package com.kap.mechanics_api.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_status_os")
public class HistoricoStatusOsJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServicoJpaEntity ordemServico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private StatusOrdemServicoJpaEntity status;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    protected HistoricoStatusOsJpaEntity() {
    }

    public HistoricoStatusOsJpaEntity(OrdemServicoJpaEntity ordemServico,
                             StatusOrdemServicoJpaEntity status,
                             LocalDateTime dataHoraInicio) {
        this.ordemServico = ordemServico;
        this.status = status;
        this.dataHoraInicio = dataHoraInicio;
    }

    public Long getId() {
        return id;
    }

    public OrdemServicoJpaEntity getOrdemServico() {
        return ordemServico;
    }

    public StatusOrdemServicoJpaEntity getStatus() {
        return status;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }
}
