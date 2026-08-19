package com.kap.mechanics_api.domain;

import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.TipoUsuario;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamento")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status_orcamento")
    private StatusOrcamento statusOrcamento;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    public Orcamento(){}

    public Orcamento(Integer id, Cliente cliente, Veiculo veiculo, BigDecimal valorTotal, StatusOrcamento statusOrcamento) {
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
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
