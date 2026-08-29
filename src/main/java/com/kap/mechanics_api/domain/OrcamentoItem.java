package com.kap.mechanics_api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orcamento_item")
public class OrcamentoItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "orcamento_id", nullable = false)
	private Orcamento orcamento;
	@ManyToOne
	@JoinColumn(name = "servico_id")
	private Servico servico;
	@ManyToOne
	@JoinColumn(name = "item_estoque_id")
	private ItemEstoque itemEstoque;
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

	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
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
