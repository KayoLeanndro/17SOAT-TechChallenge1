package com.kap.mechanics_api.adapter.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status_ordem_servico")
public class StatusOrdemServicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, unique = true, length = 30)
    private String nome;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public StatusOrdemServicoJpaEntity(){}

}
