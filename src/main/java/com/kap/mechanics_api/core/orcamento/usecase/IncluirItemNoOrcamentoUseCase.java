package com.kap.mechanics_api.core.orcamento.usecase;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.itemestoque.usecase.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.core.orcamento.domain.ComponenteOrcado;
import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ComponentesDeServicoGateway;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;
import com.kap.mechanics_api.core.servico.usecase.ServicoNaoEncontradoException;

import java.util.List;

/**
 * Inclui num orçamento pendente um serviço (com as peças padrão) ou uma peça avulsa.
 */
public class IncluirItemNoOrcamentoUseCase {

    private final OrcamentoGateway orcamentoGateway;
    private final ServicoGateway servicoGateway;
    private final ItemEstoqueGateway itemEstoqueGateway;
    private final ComponentesDeServicoGateway componentesGateway;

    public IncluirItemNoOrcamentoUseCase(OrcamentoGateway orcamentoGateway, ServicoGateway servicoGateway,
                                         ItemEstoqueGateway itemEstoqueGateway,
                                         ComponentesDeServicoGateway componentesGateway) {
        this.orcamentoGateway = orcamentoGateway;
        this.servicoGateway = servicoGateway;
        this.itemEstoqueGateway = itemEstoqueGateway;
        this.componentesGateway = componentesGateway;
    }

    public void executar(Integer orcamentoId, Integer servicoId, Integer itemEstoqueId, int quantidade) {
        Orcamento orcamento = orcamentoGateway.pesquisarPorId(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"));
        orcamento.exigirPendente();
        if ((servicoId == null) == (itemEstoqueId == null)) {
            throw new IllegalArgumentException("Informe exatamente um: servicoId ou itemEstoqueId.");
        }

        if (servicoId != null) {
            Servico servico = servicoGateway.pesquisarPorId(servicoId)
                    .orElseThrow(() -> new ServicoNaoEncontradoException(servicoId));
            orcamento.incluirServico(servicoId, servico.getValorMaoDeObra(), quantidade, componentesPadrao(servicoId));
        } else {
            orcamento.incluirItemEstoque(itemEstoqueId, buscarItem(itemEstoqueId).getValorUnitario(), quantidade);
        }
        orcamentoGateway.salvar(orcamento);
    }

    private List<ComponenteOrcado> componentesPadrao(Integer servicoId) {
        return componentesGateway.listarPorServico(servicoId).stream()
                .map(componente -> new ComponenteOrcado(componente.itemEstoqueId(), componente.quantidadePadrao(),
                        buscarItem(componente.itemEstoqueId()).getValorUnitario()))
                .toList();
    }

    private ItemEstoque buscarItem(Integer itemEstoqueId) {
        return itemEstoqueGateway.pesquisarPorId(itemEstoqueId)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(itemEstoqueId));
    }
}
