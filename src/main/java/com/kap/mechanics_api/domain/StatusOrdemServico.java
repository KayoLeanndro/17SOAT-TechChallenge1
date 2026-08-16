package com.kap.mechanics_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "status_ordem_servico")
public class StatusOrdemServico {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String nome;

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
