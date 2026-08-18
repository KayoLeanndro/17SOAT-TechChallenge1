package com.kap.mechanics_api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente_veiculo")
public class ClienteVeiculo {


    @EmbeddedId
    private ClienteVeiculoId id;

    @ManyToOne
    @MapsId("clienteId")
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @MapsId("veiculoId")
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    public ClienteVeiculo(){}

    public ClienteVeiculo(Veiculo veiculo, Cliente cliente) {
        this.veiculo = veiculo;
        this.cliente = cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public ClienteVeiculoId getId() {
        return id;
    }

    public void setId(ClienteVeiculoId id) {
        this.id = id;
    }
}
