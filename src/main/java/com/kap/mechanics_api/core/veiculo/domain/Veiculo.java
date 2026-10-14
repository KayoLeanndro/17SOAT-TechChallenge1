package com.kap.mechanics_api.core.veiculo.domain;

import java.time.LocalDateTime;

public class Veiculo {

    private Integer id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private final LocalDateTime dataCriacao;

    private Veiculo(Integer id, String placa, String marca, String modelo, Integer ano, LocalDateTime dataCriacao) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("A placa é um campo obrigatório");
        }

        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("A marca é um campo obrigatório");
        }

        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("O modelo é um campo obrigatório");
        }

        if (ano == null) {
            throw new IllegalArgumentException("O ano é um campo obrigatório");
        }

        if (dataCriacao == null) {
            throw new IllegalArgumentException("Data de criação é obrigatório");
        }

        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.dataCriacao = dataCriacao;
    }

    public static Veiculo novo(String placa, String marca, String modelo, Integer ano, LocalDateTime dataCriacao) {
        return new Veiculo(null, placa, marca, modelo, ano, dataCriacao);
    }

    public static Veiculo reconstruir(Integer id, String placa, String marca, String modelo, Integer ano, LocalDateTime dataCriacao) {
        return new Veiculo(id, placa, marca, modelo, ano, dataCriacao);
    }

    public void atualizar(String placa, String marca, String modelo, Integer ano) {
        if (placa != null && !placa.isBlank()) {
            this.placa = placa;
        }

        if (marca != null && !marca.isBlank()) {
            this.marca = marca;
        }

        if (modelo != null && !modelo.isBlank()) {
            this.modelo = modelo;
        }

        if (ano != null) {
            this.ano = ano;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
