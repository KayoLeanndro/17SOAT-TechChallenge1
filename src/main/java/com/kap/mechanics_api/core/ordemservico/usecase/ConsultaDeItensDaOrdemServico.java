package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.ordemservico.domain.StatusOrdemServico;

import java.math.BigDecimal;
import java.util.List;

public record ConsultaDeItensDaOrdemServico(Integer ordemServicoId, Integer orcamentoId, StatusOrdemServico status,
                                            BigDecimal valorTotal, List<Linha> linhas) {

    public enum Tipo {SERVICO, ESTOQUE}

    public record Linha(Integer id, Integer orcamentoItemId, Tipo tipo, Integer referenciaId, String nome,
                        int quantidade, BigDecimal valorUnitario, BigDecimal valorTotal) {
    }
}
