package com.kap.mechanics_api.adapter.persistence.entity;

import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamento")
public class OrcamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteJpaEntity cliente;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private VeiculoJpaEntity veiculo;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, name = "status_orcamento")
    private StatusOrcamento statusOrcamento;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    public OrcamentoJpaEntity(){}

    public OrcamentoJpaEntity(Integer id, ClienteJpaEntity cliente, VeiculoJpaEntity veiculo, BigDecimal valorTotal, StatusOrcamento statusOrcamento) {
        this.id = id;
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.valorTotal = valorTotal;
        this.statusOrcamento = statusOrcamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClienteJpaEntity getCliente() {
        return cliente;
    }

    public void setCliente(ClienteJpaEntity cliente) {
        this.cliente = cliente;
    }

    public VeiculoJpaEntity getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoJpaEntity veiculo) {
        this.veiculo = veiculo;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusOrcamento getStatusOrcamento() {
        return statusOrcamento;
    }

    public void setStatusOrcamento(StatusOrcamento statusOrcamento) {
        this.statusOrcamento = statusOrcamento;
    }

    public LocalDateTime getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(LocalDateTime dataResposta) {
        this.dataResposta = dataResposta;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
