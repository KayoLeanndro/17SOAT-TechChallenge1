package com.kap.mechanics_api.adapter.persistence.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ordem_servico_item")
public class OrdemServicoItemJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "ordem_servico_id", nullable = false)
	private OrdemServicoJpaEntity ordemServico;
	@ManyToOne
	@JoinColumn(name = "orcamento_item_id")
	private OrcamentoItemJpaEntity orcamentoItem;
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

	public OrdemServicoJpaEntity getOrdemServico() {
		return ordemServico;
	}

	public void setOrdemServico(OrdemServicoJpaEntity value) {
		this.ordemServico = value;
	}

	public OrcamentoItemJpaEntity getOrcamentoItem() {
		return orcamentoItem;
	}

	public void setOrcamentoItem(OrcamentoItemJpaEntity value) {
		this.orcamentoItem = value;
	}

	public ServicoJpaEntity getServico() {
		return servico;
	}

	public void setServico(ServicoJpaEntity value) {
		this.servico = value;
	}

	public ItemEstoqueJpaEntity getItemEstoque() {
		return itemEstoque;
	}

	public void setItemEstoque(ItemEstoqueJpaEntity value) {
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
