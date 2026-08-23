package com.kap.mechanics_api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "status_ordem_servico")
public class StatusOrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, unique = true, length = 30)
    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public StatusOrdemServico(){}

    public StatusOrdemServico(String nome) {
        this.nome = nome;
    }

    public StatusOrdemServico(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
