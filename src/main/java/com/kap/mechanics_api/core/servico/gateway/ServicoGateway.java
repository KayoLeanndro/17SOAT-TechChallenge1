package com.kap.mechanics_api.core.servico.gateway;

import com.kap.mechanics_api.core.servico.domain.Servico;

import java.util.List;
import java.util.Optional;

public interface ServicoGateway {

    Servico salvar(Servico servico);
    List<Servico> listarTodos();
    Optional<Servico> pesquisarPorId(Integer id);
    void excluir(Integer id);

}
