package com.kap.mechanics_api.adapter.persistence.entity;

import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.core.movimentacaoestoque.domain.TipoMovimentacaoEstoque;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoqueJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_estoque_id", nullable = false)
    private ItemEstoqueJpaEntity itemEstoque;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo", nullable = false)
    private TipoMovimentacaoEstoque tipo;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServico;

    public MovimentacaoEstoqueJpaEntity() {
    }

    public MovimentacaoEstoqueJpaEntity(ItemEstoqueJpaEntity itemEstoque,
                               TipoMovimentacaoEstoque tipo,
                               Integer quantidade,
                               UsuarioJpaEntity usuario,
                               OrdemServico ordemServico) {
        this.itemEstoque = itemEstoque;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.usuario = usuario;
        this.ordemServico = ordemServico;
        this.dataHora = LocalDateTime.now();
    }

    @PrePersist
    void preencherDataHora() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemEstoqueJpaEntity getItemEstoque() {
        return itemEstoque;
    }

    public void setItemEstoque(ItemEstoqueJpaEntity itemEstoque) {
        this.itemEstoque = itemEstoque;
    }

    public TipoMovimentacaoEstoque getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacaoEstoque tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public UsuarioJpaEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioJpaEntity usuario) {
        this.usuario = usuario;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }
}
