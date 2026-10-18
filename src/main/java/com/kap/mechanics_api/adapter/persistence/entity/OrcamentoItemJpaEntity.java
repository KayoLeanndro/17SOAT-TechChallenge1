package com.kap.mechanics_api.adapter.persistence.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orcamento_item")
public class OrcamentoItemJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "orcamento_id", nullable = false)
	private OrcamentoJpaEntity orcamento;
	@ManyToOne
	@JoinColumn(name = "servico_id")
	private ServicoJpaEntity servico;
	@ManyToOne
	@JoinColumn(name = "item_estoque_id")
	private ItemEstoqueJpaEntity itemEstoque;
	@Column(nullable = false)
	private Integer quantidade;
	@Column(name = "valor_unitario_cobrado", nullable = false)
	private BigDecimal valorUnitarioCobrado;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public OrcamentoJpaEntity getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(OrcamentoJpaEntity orcamento) {
		this.orcamento = orcamento;
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

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorUnitarioCobrado() {
		return valorUnitarioCobrado;
	}

	public void setValorUnitarioCobrado(BigDecimal valor) {
		this.valorUnitarioCobrado = valor;
	}
}
