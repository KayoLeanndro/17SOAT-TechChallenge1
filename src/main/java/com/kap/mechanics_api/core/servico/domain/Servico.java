package com.kap.mechanics_api.core.servico.domain;

import java.math.BigDecimal;

public class Servico {

    private Integer id;
    private String nome;
    private String descricao;
    private BigDecimal valorMaoDeObra;
    private Integer tempoEstimadoMin;
    private boolean ativo;

    private Servico(Integer id, String nome, String descricao, BigDecimal valorMaoDeObra,
                    Integer tempoEstimadoMin, boolean ativo) {
        this.id = id;
        this.nome = validarTexto(nome, "O nome é um campo obrigatório");
        this.descricao = validarTexto(descricao, "A descrição é um campo obrigatório");
        this.valorMaoDeObra = validarValor(valorMaoDeObra);
        this.tempoEstimadoMin = validarTempo(tempoEstimadoMin);
        this.ativo = ativo;
    }

    public static Servico novo(String nome, String descricao, BigDecimal valorMaoDeObra,
                               Integer tempoEstimadoMin, boolean ativo) {
        return new Servico(null, nome, descricao, valorMaoDeObra, tempoEstimadoMin, ativo);
    }

    public static Servico reconstruir(Integer id, String nome, String descricao, BigDecimal valorMaoDeObra,
                                      Integer tempoEstimadoMin, boolean ativo) {
        return new Servico(id, nome, descricao, valorMaoDeObra, tempoEstimadoMin, ativo);
    }

    // Atualização parcial: campos nulos (ou textos em branco) mantêm o valor atual.
    public void atualizar(String nome, String descricao, BigDecimal valorMaoDeObra,
                          Integer tempoEstimadoMin, Boolean ativo) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }

        if (descricao != null && !descricao.isBlank()) {
            this.descricao = descricao;
        }

        if (valorMaoDeObra != null) {
            this.valorMaoDeObra = validarValor(valorMaoDeObra);
        }

        if (tempoEstimadoMin != null) {
            this.tempoEstimadoMin = validarTempo(tempoEstimadoMin);
        }

        if (ativo != null) {
            this.ativo = ativo;
        }
    }

    private static String validarTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static BigDecimal validarValor(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O valor da mão de obra é um campo obrigatório");
        }
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("O valor da mão de obra não pode ser negativo");
        }
        return valor;
    }

    private static Integer validarTempo(Integer tempo) {
        if (tempo == null) {
            throw new IllegalArgumentException("O tempo estimado é um campo obrigatório");
        }
        if (tempo < 0) {
            throw new IllegalArgumentException("O tempo estimado não pode ser negativo");
        }
        return tempo;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public Integer getTempoEstimadoMin() {
        return tempoEstimadoMin;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
