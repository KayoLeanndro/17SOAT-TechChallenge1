package com.kap.mechanics_api.core.servico.usecase;

import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

import java.math.BigDecimal;

public class AtualizarServicoUseCase {

    private final ServicoGateway gateway;

    public AtualizarServicoUseCase(ServicoGateway gateway) {
        this.gateway = gateway;
    }

    public Servico executar(Integer id, String nome, String descricao, BigDecimal valorMaoDeObra,
                            Integer tempoEstimadoMin, Boolean ativo) {
        Servico servico = gateway.pesquisarPorId(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException(id));

        servico.atualizar(nome, descricao, valorMaoDeObra, tempoEstimadoMin, ativo);
        return gateway.salvar(servico);
    }
}
