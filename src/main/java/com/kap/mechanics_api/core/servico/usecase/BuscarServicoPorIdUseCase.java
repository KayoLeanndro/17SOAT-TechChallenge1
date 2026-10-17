package com.kap.mechanics_api.core.servico.usecase;

import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

public class BuscarServicoPorIdUseCase {

    private final ServicoGateway gateway;

    public BuscarServicoPorIdUseCase(ServicoGateway gateway) {
        this.gateway = gateway;
    }

    public Servico executar(Integer id) {
        return gateway.pesquisarPorId(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException(id));
    }
}
