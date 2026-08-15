package com.kap.mechanics_api.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "valor_mao_obra", nullable = false)
    private BigDecimal valorMaoDeObra;

    @Column(name = "tempoEstimadoMin", nullable = false)
    private Integer tempoEstimadoMin;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    public Servico() {

    }

    public Servico(String nome, String descricao, BigDecimal valorMaoDeObra, Integer tempoEstimadoMin, boolean ativo) {
        this.nome = nome;
        this.descricao = descricao;
        this.valorMaoDeObra = valorMaoDeObra;
        this.tempoEstimadoMin = tempoEstimadoMin;
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public void setValorMaoDeObra(BigDecimal valorMaoDeObra) {
        this.valorMaoDeObra = valorMaoDeObra;
    }

    public Integer getTempoEstimadoMin() {
        return tempoEstimadoMin;
    }

    public void setTempoEstimadoMin(Integer tempoEstimadoMin) {
        this.tempoEstimadoMin = tempoEstimadoMin;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
