package com.kap.mechanics_api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "status_ordem_servico")
public class StatusOrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome",nullable = false, unique = true, length = 30)
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusOrdemServico(){}

    public StatusOrdemServico( String descricao) {
        this.descricao = descricao;
    }

    public StatusOrdemServico(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
}
