package com.kap.mechanics_api.core.servico.usecase;

import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

import java.math.BigDecimal;

public class CadastrarServicoUseCase {

    private final ServicoGateway gateway;

    public CadastrarServicoUseCase(ServicoGateway gateway) {
        this.gateway = gateway;
    }

    public Servico executar(String nome, String descricao, BigDecimal valorMaoDeObra,
                            Integer tempoEstimadoMin, boolean ativo) {
        return gateway.salvar(Servico.novo(nome, descricao, valorMaoDeObra, tempoEstimadoMin, ativo));
    }
}
