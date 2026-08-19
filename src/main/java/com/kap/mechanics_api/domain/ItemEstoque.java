package com.kap.mechanics_api.domain;

import com.kap.mechanics_api.enums.TipoItemEstoque;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "item_estoque")
public class ItemEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_estoque_nome")
    private String nome;

    @Column(name = "item_estoque_descricao")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item_estoque", nullable = false)
    private TipoItemEstoque tipoItemEstoque;

    @Column(name = "item_estoque_valor")
    private BigDecimal valorUnitario;

    @Column(name = "quantidade_atual")
    private int quantidadeAtual;

    @Column(name = "quantidade_minima")
    private int quantidadeMinima;

    @Column(name = "ativo")
    private boolean ativo;

    public ItemEstoque() {
    }

    public ItemEstoque(String nome, String descricao, TipoItemEstoque tipoItemEstoque,
                       BigDecimal valorUnitario, int quantidadeAtual, int quantidadeMinima, boolean ativo) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipoItemEstoque = tipoItemEstoque;
        this.valorUnitario = valorUnitario;
        this.quantidadeAtual = quantidadeAtual;
        this.quantidadeMinima = quantidadeMinima;
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoItemEstoque getTipoItemEstoque() {
        return tipoItemEstoque;
    }

    public void setTipoItemEstoque(TipoItemEstoque tipoItemEstoque) {
        this.tipoItemEstoque = tipoItemEstoque;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(int quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemEstoque that)) return false;
        return quantidadeAtual == that.quantidadeAtual
                && quantidadeMinima == that.quantidadeMinima
                && ativo == that.ativo && Objects.equals(id, that.id)
                && Objects.equals(nome, that.nome)
                && Objects.equals(descricao, that.descricao)
                && tipoItemEstoque == that.tipoItemEstoque
                && Objects.equals(valorUnitario, that.valorUnitario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descricao, tipoItemEstoque,
                valorUnitario, quantidadeAtual, quantidadeMinima, ativo);
    }

    @Override
    public String toString() {
        return "ItemEstoque{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", tipoItemEstoque=" + tipoItemEstoque +
                ", valorUnitario=" + valorUnitario +
                ", quantidadeAtual=" + quantidadeAtual +
                ", quantidadeMinima=" + quantidadeMinima +
                ", ativo=" + ativo +
                '}';
    }
}
