package com.kap.mechanics_api.core.orcamento.usecase;

import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;

import java.math.BigDecimal;
import java.util.List;

public record ConsultaDeItensDoOrcamento(Integer orcamentoId, StatusOrcamento status, BigDecimal valorTotal,
                                         List<Linha> linhas) {

    public enum Tipo {SERVICO, ESTOQUE}

    public record Linha(Integer id, Tipo tipo, Integer referenciaId, String nome, int quantidade,
                        BigDecimal valorUnitario, BigDecimal valorTotal) {
    }
}
