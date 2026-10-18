package com.kap.mechanics_api.adapter.persistence.entity;

import com.kap.mechanics_api.domain.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico")
public class OrdemServicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "orcamento_id", nullable = false, unique = true)
    private OrcamentoJpaEntity orcamento;

    @ManyToOne
    @JoinColumn(name = "usuario_atendente_id", nullable = false)
    private Usuario usuarioAtendente;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusOrdemServicoJpaEntity statusOrdemServico;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrcamentoJpaEntity getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(OrcamentoJpaEntity orcamento) {
        this.orcamento = orcamento;
    }

    public Usuario getUsuarioAtendente() {
        return usuarioAtendente;
    }

    public void setUsuarioAtendente(Usuario usuarioAtendente) {
        this.usuarioAtendente = usuarioAtendente;
    }

    public StatusOrdemServicoJpaEntity getStatusOrdemServico() {
        return statusOrdemServico;
    }

    public void setStatusOrdemServico(StatusOrdemServicoJpaEntity statusOrdemServico) {
        this.statusOrdemServico = statusOrdemServico;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDateTime dataEntrega) {
        this.dataEntrega = dataEntrega;
    }
}
