package com.kap.mechanics_api.adapter.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente_veiculo")
public class ClienteVeiculoJpaEntity {


    @EmbeddedId
    private ClienteVeiculoJpaId id;

    @ManyToOne
    @MapsId("clienteId")
    @JoinColumn(name = "cliente_id")
    private ClienteJpaEntity clienteJpaEntity;

    @ManyToOne
    @MapsId("veiculoId")
    @JoinColumn(name = "veiculo_id")
    private VeiculoJpaEntity veiculo;

    public ClienteVeiculoJpaEntity(){}

    public ClienteVeiculoJpaEntity(VeiculoJpaEntity veiculo, ClienteJpaEntity clienteJpaEntity) {
        this.veiculo = veiculo;
        this.clienteJpaEntity = clienteJpaEntity;
    }

    public VeiculoJpaEntity getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoJpaEntity veiculo) {
        this.veiculo = veiculo;
    }

    public ClienteJpaEntity getCliente() {
        return clienteJpaEntity;
    }

    public void setCliente(ClienteJpaEntity clienteJpaEntity) {
        this.clienteJpaEntity = clienteJpaEntity;
    }

    public ClienteVeiculoJpaId getId() {
        return id;
    }

    public void setId(ClienteVeiculoJpaId id) {
        this.id = id;
    }
}
