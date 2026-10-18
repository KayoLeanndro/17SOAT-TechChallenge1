package com.kap.mechanics_api.core.ordemservico.gateway;

import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServicoResumo;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoGateway {

    // Grava a ordem, o histórico e as linhas novas; o id decide entre INSERT e UPDATE.
    OrdemServico salvar(OrdemServico ordemServico);
    Optional<OrdemServico> pesquisarPorId(Integer id);
    Optional<OrdemServico> pesquisarPorOrcamentoId(Integer orcamentoId);
    boolean existePorOrcamentoId(Integer orcamentoId);
    List<OrdemServicoResumo> listarResumos();
    // Mais recentes primeiro.
    List<OrdemServicoResumo> listarResumosPorCliente(Integer clienteId);

}
