package com.kap.mechanics_api.core.movimentacaoestoque.gateway;

import java.util.List;
import java.util.Optional;

/**
 * Porta de leitura sobre a ordem de serviço, ainda não migrada. Quando o contexto Ordem de Serviço
 * ganhar o seu gateway, esta porta pode ser implementada em cima dele.
 */
public interface OrdemServicoEstoqueGateway {

    Optional<OrdemServicoParaEstoque> buscarPorId(Integer ordemServicoId);

    // Linhas já registradas na ordem (serviços e itens de estoque).
    List<QuantidadeDeItem> listarLinhasDaOrdem(Integer ordemServicoId);

    // Peças padrão dos serviços do orçamento da ordem: usado quando a ordem não tem linhas próprias.
    List<QuantidadeDeItem> listarComponentesDosServicosOrcados(Integer ordemServicoId);

}
