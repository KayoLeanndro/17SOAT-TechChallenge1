package com.kap.mechanics_api.domain;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class ClienteVeiculoId {

    private Integer clienteId;
    private Integer veiculoId;

    public ClienteVeiculoId(Integer clienteId, Integer veiculoId) {
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
    }

    public ClienteVeiculoId(){}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClienteVeiculoId that = (ClienteVeiculoId) o;
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
