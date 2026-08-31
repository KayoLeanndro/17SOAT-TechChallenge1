package com.kap.mechanics_api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ordem_servico_item")
public class OrdemServicoItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "ordem_servico_id", nullable = false)
	private OrdemServico ordemServico;
	@ManyToOne
	@JoinColumn(name = "orcamento_item_id")
	private OrcamentoItem orcamentoItem;
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

	public OrdemServico getOrdemServico() {
		return ordemServico;
	}

	public void setOrdemServico(OrdemServico value) {
		this.ordemServico = value;
	}

	public OrcamentoItem getOrcamentoItem() {
		return orcamentoItem;
	}

	public void setOrcamentoItem(OrcamentoItem value) {
		this.orcamentoItem = value;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico value) {
		this.servico = value;
	}

	public ItemEstoque getItemEstoque() {
		return itemEstoque;
	}

	public void setItemEstoque(ItemEstoque value) {
		this.itemEstoque = value;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer value) {
		this.quantidade = value;
	}

	public BigDecimal getValorUnitarioCobrado() {
		return valorUnitarioCobrado;
	}

	public void setValorUnitarioCobrado(BigDecimal value) {
		this.valorUnitarioCobrado = value;
	}
}
