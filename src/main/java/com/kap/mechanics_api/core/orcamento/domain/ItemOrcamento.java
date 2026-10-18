package com.kap.mechanics_api.core.orcamento.domain;

import java.math.BigDecimal;

/**
 * Linha do orçamento: ou é um serviço (mão de obra) ou uma peça do estoque, nunca os dois.
 * Referencia serviço e item de estoque só pelo id.
 */
public class ItemOrcamento {

    private final Integer id;
    private final Integer servicoId;
    private final Integer itemEstoqueId;
    private final int quantidade;
    private final BigDecimal valorUnitario;

    private ItemOrcamento(Integer id, Integer servicoId, Integer itemEstoqueId, int quantidade,
                          BigDecimal valorUnitario) {
        if ((servicoId == null) == (itemEstoqueId == null)) {
            throw new IllegalArgumentException("Informe exatamente um: servicoId ou itemEstoqueId.");
        }
        if (quantidade < 1) {
            throw new IllegalArgumentException("A quantidade deve ser no mínimo 1");
        }
        if (valorUnitario == null || valorUnitario.signum() < 0) {
            throw new IllegalArgumentException("O valor unitário não pode ser nulo nem negativo");
        }
        this.id = id;
        this.servicoId = servicoId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public static ItemOrcamento doServico(Integer servicoId, int quantidade, BigDecimal valorUnitario) {
        return new ItemOrcamento(null, servicoId, null, quantidade, valorUnitario);
    }

    public static ItemOrcamento doEstoque(Integer itemEstoqueId, int quantidade, BigDecimal valorUnitario) {
        return new ItemOrcamento(null, null, itemEstoqueId, quantidade, valorUnitario);
    }

    public static ItemOrcamento reconstruir(Integer id, Integer servicoId, Integer itemEstoqueId, int quantidade,
                                            BigDecimal valorUnitario) {
        return new ItemOrcamento(id, servicoId, itemEstoqueId, quantidade, valorUnitario);
    }

    public BigDecimal valorTotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public boolean isServico() {
        return servicoId != null;
    }

    public Integer getId() {
        return id;
    }

    public Integer getServicoId() {
        return servicoId;
    }

    public Integer getItemEstoqueId() {
        return itemEstoqueId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }
}
