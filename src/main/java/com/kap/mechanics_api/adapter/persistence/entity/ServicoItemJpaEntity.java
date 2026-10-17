package com.kap.mechanics_api.adapter.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "servico_item")
public class ServicoItemJpaEntity {

    @EmbeddedId
    private ServicoItemJpaId id;

    @ManyToOne
    @MapsId("servicoId")
    @JoinColumn(name = "servico_id", nullable = false)
    private ServicoJpaEntity servico;

    @ManyToOne
    @MapsId("itemEstoqueId")
    @JoinColumn(name = "item_estoque_id", nullable = false)
    private ItemEstoqueJpaEntity itemEstoque;

    @Column(name = "quantidade_padrao", nullable = false)
    private Integer quantidadePadrao;

    public ServicoItemJpaEntity() {
    }

    public ServicoItemJpaId getId() {
        return id;
    }

    public void setId(ServicoItemJpaId id) {
        this.id = id;
    }

    public ServicoJpaEntity getServico() {
        return servico;
    }

    public void setServico(ServicoJpaEntity servico) {
        this.servico = servico;
    }

    public ItemEstoqueJpaEntity getItemEstoque() {
        return itemEstoque;
    }

    public void setItemEstoque(ItemEstoqueJpaEntity itemEstoque) {
        this.itemEstoque = itemEstoque;
    }

    public Integer getQuantidadePadrao() {
        return quantidadePadrao;
    }

    public void setQuantidadePadrao(Integer quantidadePadrao) {
        this.quantidadePadrao = quantidadePadrao;
    }
}