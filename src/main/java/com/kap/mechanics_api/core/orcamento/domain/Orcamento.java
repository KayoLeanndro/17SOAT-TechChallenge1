package com.kap.mechanics_api.core.orcamento.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Orçamento de uma oficina para um cliente e um veículo. Guarda as próprias linhas e os serviços
 * orçados, e referencia as demais entidades só pelo id.
 */
public class Orcamento {

    private final Integer id;
    private final Integer clienteId;
    private final Integer veiculoId;
    private BigDecimal valorTotal;
    private StatusOrcamento status;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataResposta;
    private final List<ItemOrcamento> itens;
    private final List<ServicoOrcado> servicosOrcados;

    private Orcamento(Integer id, Integer clienteId, Integer veiculoId, BigDecimal valorTotal,
                      StatusOrcamento status, LocalDateTime dataCriacao, LocalDateTime dataResposta,
                      List<ItemOrcamento> itens, List<ServicoOrcado> servicosOrcados) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O cliente é um campo obrigatório");
        }
        if (veiculoId == null) {
            throw new IllegalArgumentException("O veículo é um campo obrigatório");
        }
        this.id = id;
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataResposta = dataResposta;
        this.itens = new ArrayList<>(itens);
        this.servicosOrcados = new ArrayList<>(servicosOrcados);
    }

    public static Orcamento novo(Integer clienteId, Integer veiculoId, LocalDateTime agora) {
        return new Orcamento(null, clienteId, veiculoId, BigDecimal.ZERO, StatusOrcamento.PENDENTE, agora, null,
                List.of(), List.of());
    }

    public static Orcamento reconstruir(Integer id, Integer clienteId, Integer veiculoId, BigDecimal valorTotal,
                                        StatusOrcamento status, LocalDateTime dataCriacao,
                                        LocalDateTime dataResposta, List<ItemOrcamento> itens,
                                        List<ServicoOrcado> servicosOrcados) {
        return new Orcamento(id, clienteId, veiculoId, valorTotal, status, dataCriacao, dataResposta, itens,
                servicosOrcados);
    }

    /**
     * Geração do orçamento: o serviço entra uma vez (mão de obra) seguido de suas peças padrão, e o valor
     * cobrado do serviço é a mão de obra somada às peças.
     */
    public void orcarServico(Integer servicoId, BigDecimal valorMaoDeObra, List<ComponenteOrcado> componentes) {
        BigDecimal valorServico = valorMaoDeObra;
        for (ComponenteOrcado componente : componentes) {
            valorServico = valorServico.add(
                    componente.valorUnitario().multiply(BigDecimal.valueOf(componente.quantidade())));
        }
        servicosOrcados.add(new ServicoOrcado(servicoId, valorServico));
        adicionarLinhasDoServico(servicoId, valorMaoDeObra, 1, componentes);
    }

    /**
     * Inclusão avulsa depois de gerado: só vale enquanto o orçamento está pendente.
     */
    public void incluirServico(Integer servicoId, BigDecimal valorMaoDeObra, int quantidade,
                               List<ComponenteOrcado> componentesPadrao) {
        exigirPendente();
        List<ComponenteOrcado> componentes = new ArrayList<>();
        for (ComponenteOrcado padrao : componentesPadrao) {
            componentes.add(new ComponenteOrcado(padrao.itemEstoqueId(),
                    Math.multiplyExact(quantidade, padrao.quantidade()), padrao.valorUnitario()));
        }
        adicionarLinhasDoServico(servicoId, valorMaoDeObra, quantidade, componentes);
    }

    public void incluirItemEstoque(Integer itemEstoqueId, BigDecimal valorUnitario, int quantidade) {
        exigirPendente();
        adicionar(ItemOrcamento.doEstoque(itemEstoqueId, quantidade, valorUnitario));
    }

    /**
     * Muda o status do orçamento. A data de resposta só é marcada quando o cliente aprova ou rejeita.
     */
    public void alterarStatus(StatusOrcamento novoStatus, LocalDateTime agora) {
        this.status = novoStatus;
        if (novoStatus == StatusOrcamento.APROVADO || novoStatus == StatusOrcamento.REJEITADO) {
            this.dataResposta = agora;
        }
    }

    public void exigirPendente() {
        if (status != StatusOrcamento.PENDENTE) {
            throw new IllegalArgumentException("Só é possível incluir itens em orçamento pendente.");
        }
    }

    private void adicionarLinhasDoServico(Integer servicoId, BigDecimal valorMaoDeObra, int quantidade,
                                          List<ComponenteOrcado> componentes) {
        adicionar(ItemOrcamento.doServico(servicoId, quantidade, valorMaoDeObra));
        for (ComponenteOrcado componente : componentes) {
            adicionar(ItemOrcamento.doEstoque(componente.itemEstoqueId(), componente.quantidade(),
                    componente.valorUnitario()));
        }
    }

    private void adicionar(ItemOrcamento item) {
        itens.add(item);
        valorTotal = valorTotal.add(item.valorTotal());
    }

    public Integer getId() {
        return id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public Integer getVeiculoId() {
        return veiculoId;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public StatusOrcamento getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataResposta() {
        return dataResposta;
    }

    public List<ItemOrcamento> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public List<ServicoOrcado> getServicosOrcados() {
        return Collections.unmodifiableList(servicosOrcados);
    }
}
