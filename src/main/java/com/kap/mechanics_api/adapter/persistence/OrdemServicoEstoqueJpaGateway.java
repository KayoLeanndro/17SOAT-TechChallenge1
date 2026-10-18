package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoParaEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.QuantidadeDeItem;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;
import com.kap.mechanics_api.core.servico.gateway.ComponentesDeServicoGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Ponte entre o estoque e a ordem de serviço: responde ao estoque, na visão dele, usando os gateways
 * dos contextos Ordem de Serviço, Orçamento e Serviço.
 */
@Component
public class OrdemServicoEstoqueJpaGateway implements OrdemServicoEstoqueGateway {

    private final OrdemServicoGateway ordemServicoGateway;
    private final OrcamentoGateway orcamentoGateway;
    private final ComponentesDeServicoGateway componentesGateway;

    public OrdemServicoEstoqueJpaGateway(OrdemServicoGateway ordemServicoGateway, OrcamentoGateway orcamentoGateway,
                                         ComponentesDeServicoGateway componentesGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.orcamentoGateway = orcamentoGateway;
        this.componentesGateway = componentesGateway;
    }

    @Override
    public Optional<OrdemServicoParaEstoque> buscarPorId(Integer ordemServicoId) {
        return ordemServicoGateway.pesquisarPorId(ordemServicoId).map(this::paraVisaoDoEstoque);
    }

    @Override
    public List<QuantidadeDeItem> listarLinhasDaOrdem(Integer ordemServicoId) {
        return ordemServicoGateway.pesquisarPorId(ordemServicoId)
                .map(os -> os.getItens().stream()
                        .map(linha -> new QuantidadeDeItem(linha.getItemEstoqueId(), linha.getQuantidade()))
                        .toList())
                .orElse(List.of());
    }

    @Override
    public List<QuantidadeDeItem> listarComponentesDosServicosOrcados(Integer ordemServicoId) {
        return ordemServicoGateway.pesquisarPorId(ordemServicoId)
                .flatMap(os -> orcamentoGateway.pesquisarPorId(os.getOrcamentoId()))
                .map(orcamento -> orcamento.getServicosOrcados().stream()
                        .flatMap(servicoOrcado -> componentesGateway.listarPorServico(servicoOrcado.servicoId())
                                .stream())
                        .map(componente -> new QuantidadeDeItem(
                                componente.itemEstoqueId(), componente.quantidadePadrao()))
                        .toList())
                .orElse(List.of());
    }

    private OrdemServicoParaEstoque paraVisaoDoEstoque(OrdemServico os) {
        return new OrdemServicoParaEstoque(os.getId(), os.estaEmExecucao(), os.getAtendenteId());
    }
}
