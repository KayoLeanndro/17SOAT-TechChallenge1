package com.kap.mechanics_api.domain;

import com.kap.mechanics_api.adapter.persistence.entity.ClienteJpaEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "cliente_veiculo")
public class ClienteVeiculo {


    @EmbeddedId
    private ClienteVeiculoId id;

    @ManyToOne
    @MapsId("clienteId")
    @JoinColumn(name = "cliente_id")
    private ClienteJpaEntity clienteJpaEntity;

    @ManyToOne
    @MapsId("veiculoId")
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    public ClienteVeiculo(){}

    public ClienteVeiculo(Veiculo veiculo, ClienteJpaEntity clienteJpaEntity) {
        this.veiculo = veiculo;
        this.clienteJpaEntity = clienteJpaEntity;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public ClienteJpaEntity getCliente() {
        return clienteJpaEntity;
    }

    public void setCliente(ClienteJpaEntity clienteJpaEntity) {
        this.clienteJpaEntity = clienteJpaEntity;
    }

    public ClienteVeiculoId getId() {
        return id;
    }

    public void setId(ClienteVeiculoId id) {
        this.id = id;
    }
}
