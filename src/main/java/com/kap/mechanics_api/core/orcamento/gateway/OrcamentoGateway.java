package com.kap.mechanics_api.core.orcamento.gateway;

import com.kap.mechanics_api.core.orcamento.domain.Orcamento;

import java.util.Optional;

public interface OrcamentoGateway {

    // Grava o orçamento e as linhas/serviços novos; o id decide entre INSERT e UPDATE.
    Orcamento salvar(Orcamento orcamento);
    Optional<Orcamento> pesquisarPorId(Integer id);

}
