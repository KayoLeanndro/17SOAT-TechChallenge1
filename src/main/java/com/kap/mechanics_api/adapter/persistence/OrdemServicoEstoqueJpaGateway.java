package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoEstoqueGateway;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.OrdemServicoParaEstoque;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.QuantidadeDeItem;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.OrdemServicoItemRepository;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Enquanto a Ordem de Serviço não é migrada, lê dela, via repositórios JPA, só o que o estoque precisa.
 */
@Component
public class OrdemServicoEstoqueJpaGateway implements OrdemServicoEstoqueGateway {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoItemRepository ordemServicoItemRepository;
    private final OrcamentoServicoRepository orcamentoServicoRepository;
    private final ServicoItemRepository servicoItemRepository;

    public OrdemServicoEstoqueJpaGateway(OrdemServicoRepository ordemServicoRepository,
                                         OrdemServicoItemRepository ordemServicoItemRepository,
                                         OrcamentoServicoRepository orcamentoServicoRepository,
                                         ServicoItemRepository servicoItemRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoItemRepository = ordemServicoItemRepository;
        this.orcamentoServicoRepository = orcamentoServicoRepository;
        this.servicoItemRepository = servicoItemRepository;
    }

    @Override
    public Optional<OrdemServicoParaEstoque> buscarPorId(Integer ordemServicoId) {
        return ordemServicoRepository.findById(ordemServicoId).map(this::paraVisaoDoEstoque);
    }

    @Override
    public List<QuantidadeDeItem> listarLinhasDaOrdem(Integer ordemServicoId) {
        return ordemServicoItemRepository.findByOrdemServico_Id(ordemServicoId).stream()
                .map(linha -> new QuantidadeDeItem(
                        linha.getItemEstoque() == null ? null : linha.getItemEstoque().getId(),
                        linha.getQuantidade()))
                .toList();
    }

    @Override
    public List<QuantidadeDeItem> listarComponentesDosServicosOrcados(Integer ordemServicoId) {
        return ordemServicoRepository.findById(ordemServicoId)
                .map(os -> orcamentoServicoRepository.findByOrcamento_Id(os.getOrcamento().getId()).stream()
                        .flatMap(servicoOrcado -> servicoItemRepository
                                .findByServico_Id(servicoOrcado.getServico().getId()).stream())
                        .map(componente -> new QuantidadeDeItem(
                                componente.getItemEstoque().getId(), componente.getQuantidadePadrao()))
                        .toList())
                .orElse(List.of());
    }

    private OrdemServicoParaEstoque paraVisaoDoEstoque(OrdemServico os) {
        boolean emExecucao = StatusOrdemServicoEnum.EM_EXECUCAO.name().equals(os.getStatusOrdemServico().getNome());
        return new OrdemServicoParaEstoque(os.getId(), emExecucao, os.getUsuarioAtendente().getId());
    }
}
