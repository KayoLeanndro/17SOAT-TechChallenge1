package com.kap.mechanics_api.core.servico.usecase;

import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

public class ExcluirServicoUseCase {

    private final ServicoGateway gateway;

    public ExcluirServicoUseCase(ServicoGateway gateway) {
        this.gateway = gateway;
    }

    public void executar(Integer id) {
        Servico servico = gateway.pesquisarPorId(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException(id));
        gateway.excluir(servico.getId());
    }
}
