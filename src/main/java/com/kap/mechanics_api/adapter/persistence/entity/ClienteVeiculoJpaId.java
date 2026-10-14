package com.kap.mechanics_api.adapter.persistence.entity;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class ClienteVeiculoJpaId {

    private Integer clienteId;
    private Integer veiculoId;

    public ClienteVeiculoJpaId(Integer clienteId, Integer veiculoId) {
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
    }

    public ClienteVeiculoJpaId(){}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClienteVeiculoJpaId that = (ClienteVeiculoJpaId) o;
        return Objects.equals(clienteId, that.clienteId) && Objects.equals(veiculoId, that.veiculoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteId, veiculoId);
    }

    public Integer getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(Integer veiculoId) {
        this.veiculoId = veiculoId;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
}
