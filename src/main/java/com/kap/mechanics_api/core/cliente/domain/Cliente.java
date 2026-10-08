package com.kap.mechanics_api.core.cliente.domain;

import java.time.LocalDateTime;

public class Cliente {

    private Integer id;
    private String nome;
    private CpfCnpj cpfCnpj;
    private String telefone;
    private String email;
    private final LocalDateTime dataCriacao;

    private Cliente (Integer id,String nome, CpfCnpj cpfCnpj, String telefone, String email, LocalDateTime dataCriacao){
        if(nome == null || nome.isBlank()){
            throw new IllegalArgumentException("O nome é um campo obrigatório");
        }

        if(cpfCnpj == null){
            throw new IllegalArgumentException("CPF/CNPJ é obrigatório");
        }

        if(dataCriacao == null){
            throw new IllegalArgumentException("Data de criação é obrigatório");
        }

        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.telefone = telefone;
        this.dataCriacao= dataCriacao;
    }

    public static Cliente novo(String nome, CpfCnpj cpfCnpj, String telefone, String email, LocalDateTime dataCriacao){
        return new Cliente(null, nome, cpfCnpj,telefone, email, dataCriacao);
    }

    public static Cliente reconstruir(Integer id,String nome, CpfCnpj cpfCnpj, String telefone, String email, LocalDateTime dataCriacao){
        return new Cliente(id,nome,cpfCnpj,telefone,email,dataCriacao);
    }

    public void atualizar(String nome, CpfCnpj cpfCnpj, String telefone, String email){
        if(nome != null && !nome.isBlank()){
            this.nome = nome;
        }

        if(cpfCnpj != null){
            this.cpfCnpj = cpfCnpj;
        }

        if(telefone != null && !telefone.isBlank()){
            this.telefone = telefone;
        }

        if(email != null && !email.isBlank()){
            this.email = email;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public CpfCnpj getCpfCnpj() {
        return cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}


