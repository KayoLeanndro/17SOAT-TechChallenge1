package com.kap.mechanics_api.core.itemestoque.domain;

import java.math.BigDecimal;

public class ItemEstoque {

    private Integer id;
    private String nome;
    private String descricao;
    private TipoItemEstoque tipoItemEstoque;
    private BigDecimal valorUnitario;
    private int quantidadeAtual;
    private int quantidadeMinima;
    private boolean ativo;

    private ItemEstoque(Integer id, String nome, String descricao, TipoItemEstoque tipoItemEstoque,
                        BigDecimal valorUnitario, int quantidadeAtual, int quantidadeMinima, boolean ativo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é um campo obrigatório");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição é um campo obrigatório");
        }

        if (tipoItemEstoque == null) {
            throw new IllegalArgumentException("O tipo do item é um campo obrigatório");
        }

        if (quantidadeAtual < 0) {
            throw new IllegalArgumentException("A quantidade atual não pode ser negativa");
        }

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipoItemEstoque = tipoItemEstoque;
        this.valorUnitario = validarValor(valorUnitario);
        this.quantidadeAtual = quantidadeAtual;
        this.quantidadeMinima = validarQuantidadeMinima(quantidadeMinima);
        this.ativo = ativo;
    }

    // Todo item nasce com saldo zero: a quantidade só muda por movimentação de estoque.
    public static ItemEstoque novo(String nome, String descricao, TipoItemEstoque tipoItemEstoque,
                                   BigDecimal valorUnitario, int quantidadeMinima, boolean ativo) {
        return new ItemEstoque(null, nome, descricao, tipoItemEstoque, valorUnitario, 0, quantidadeMinima, ativo);
    }

    public static ItemEstoque reconstruir(Integer id, String nome, String descricao, TipoItemEstoque tipoItemEstoque,
                                          BigDecimal valorUnitario, int quantidadeAtual, int quantidadeMinima,
                                          boolean ativo) {
        return new ItemEstoque(id, nome, descricao, tipoItemEstoque, valorUnitario, quantidadeAtual,
                quantidadeMinima, ativo);
    }

    // Atualização parcial; a quantidade atual não é editável por aqui.
    public void atualizar(String nome, String descricao, TipoItemEstoque tipoItemEstoque,
                          BigDecimal valorUnitario, Integer quantidadeMinima, Boolean ativo) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }

        if (descricao != null && !descricao.isBlank()) {
            this.descricao = descricao;
        }

        if (tipoItemEstoque != null) {
            this.tipoItemEstoque = tipoItemEstoque;
        }

        if (valorUnitario != null) {
            this.valorUnitario = validarValor(valorUnitario);
        }

        if (quantidadeMinima != null) {
            this.quantidadeMinima = validarQuantidadeMinima(quantidadeMinima);
        }

        if (ativo != null) {
            this.ativo = ativo;
        }
    }

    public void registrarEntrada(int quantidade) {
        validarQuantidadePositiva(quantidade);
        validarAtivo();
        this.quantidadeAtual = Math.addExact(quantidadeAtual, quantidade);
    }

    public void registrarSaida(int quantidade) {
        validarQuantidadePositiva(quantidade);
        validarAtivo();
        if (quantidadeAtual < quantidade) {
            throw new EstoqueInsuficienteException(id, quantidadeAtual, quantidade);
        }
        this.quantidadeAtual -= quantidade;
    }

    private void validarAtivo() {
        if (!ativo) {
            throw new ItemEstoqueInativoException(id);
        }
    }

    private static void validarQuantidadePositiva(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }
    }

    private static BigDecimal validarValor(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O valor unitário é um campo obrigatório");
        }
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("O valor unitário não pode ser negativo");
        }
        return valor;
    }

    private static int validarQuantidadeMinima(int quantidadeMinima) {
        if (quantidadeMinima < 0) {
            throw new IllegalArgumentException("A quantidade mínima não pode ser negativa");
        }
        return quantidadeMinima;
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

    public TipoItemEstoque getTipoItemEstoque() {
        return tipoItemEstoque;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
