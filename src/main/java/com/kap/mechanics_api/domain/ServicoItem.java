package com.kap.mechanics_api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "servico_item")
public class ServicoItem {

    @EmbeddedId
    private ServicoItemId id;

    @ManyToOne
    @MapsId("servicoId")
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne
    @MapsId("itemEstoqueId")
    @JoinColumn(name = "item_estoque_id", nullable = false)
    private ItemEstoque itemEstoque;

    @Column(name = "quantidade_padrao", nullable = false)
    private Integer quantidadePadrao;

    public ServicoItem() {
    }

    public ServicoItemId getId() {
        return id;
    }

    public void setId(ServicoItemId id) {
        this.id = id;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public ItemEstoque getItemEstoque() {
        return itemEstoque;
    }

    public void setItemEstoque(ItemEstoque itemEstoque) {
        this.itemEstoque = itemEstoque;
    }

    public Integer getQuantidadePadrao() {
        return quantidadePadrao;
    }

    public void setQuantidadePadrao(Integer quantidadePadrao) {
        this.quantidadePadrao = quantidadePadrao;
    }
}