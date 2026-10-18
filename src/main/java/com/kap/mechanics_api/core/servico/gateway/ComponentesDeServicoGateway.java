package com.kap.mechanics_api.core.servico.gateway;

import java.util.List;

public interface ComponentesDeServicoGateway {

    List<ComponenteDeServico> listarPorServico(Integer servicoId);

}
