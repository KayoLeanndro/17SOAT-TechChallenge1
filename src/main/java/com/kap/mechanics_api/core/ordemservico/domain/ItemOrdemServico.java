package com.kap.mechanics_api.core.ordemservico.domain;

import java.math.BigDecimal;

/**
 * Linha da ordem de serviço: um serviço ou uma peça de estoque. Quando veio do orçamento,
 * guarda o id da linha de origem.
 */
public class ItemOrdemServico {

    private final Integer id;
    private final Integer orcamentoItemId;
    private final Integer servicoId;
    private final Integer itemEstoqueId;
    private final int quantidade;
    private final BigDecimal valorUnitario;

    private ItemOrdemServico(Integer id, Integer orcamentoItemId, Integer servicoId, Integer itemEstoqueId,
                             int quantidade, BigDecimal valorUnitario) {
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
        this.orcamentoItemId = orcamentoItemId;
        this.servicoId = servicoId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public static ItemOrdemServico novo(Integer orcamentoItemId, Integer servicoId, Integer itemEstoqueId,
                                        int quantidade, BigDecimal valorUnitario) {
        return new ItemOrdemServico(null, orcamentoItemId, servicoId, itemEstoqueId, quantidade, valorUnitario);
    }

    public static ItemOrdemServico reconstruir(Integer id, Integer orcamentoItemId, Integer servicoId,
                                               Integer itemEstoqueId, int quantidade, BigDecimal valorUnitario) {
        return new ItemOrdemServico(id, orcamentoItemId, servicoId, itemEstoqueId, quantidade, valorUnitario);
    }

    public BigDecimal valorTotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Integer getId() {
        return id;
    }

    public Integer getOrcamentoItemId() {
        return orcamentoItemId;
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
