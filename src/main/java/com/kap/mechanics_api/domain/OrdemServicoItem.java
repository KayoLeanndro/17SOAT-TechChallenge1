package com.kap.mechanics_api.domain;

import com.kap.mechanics_api.adapter.persistence.entity.ItemEstoqueJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.ServicoJpaEntity;

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
