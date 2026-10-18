package com.kap.mechanics_api.core.ordemservico.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordem de serviço aberta a partir de um orçamento. Controla o próprio fluxo de status (com histórico)
 * e as linhas de serviço/peças; orçamento e atendente entram só pelo id.
 */
public class OrdemServico {

    private final Integer id;
    private final Integer orcamentoId;
    private final Integer atendenteId;
    private StatusOrdemServico status;
    private final LocalDateTime dataAbertura;
    private LocalDateTime dataEntrega;
    private final List<HistoricoDeStatus> historico;
    private final List<ItemOrdemServico> itens;

    private OrdemServico(Integer id, Integer orcamentoId, Integer atendenteId, StatusOrdemServico status,
                         LocalDateTime dataAbertura, LocalDateTime dataEntrega,
                         List<HistoricoDeStatus> historico, List<ItemOrdemServico> itens) {
        if (orcamentoId == null) {
            throw new IllegalArgumentException("O orçamento é um campo obrigatório");
        }
        if (atendenteId == null) {
            throw new IllegalArgumentException("O atendente é um campo obrigatório");
        }
        this.id = id;
        this.orcamentoId = orcamentoId;
        this.atendenteId = atendenteId;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.dataEntrega = dataEntrega;
        this.historico = new ArrayList<>(historico);
        this.itens = new ArrayList<>(itens);
    }

    // Toda ordem nasce aguardando a aprovação do orçamento.
    public static OrdemServico abrir(Integer orcamentoId, Integer atendenteId, LocalDateTime agora) {
        List<HistoricoDeStatus> historico = List.of(
                HistoricoDeStatus.abrir(StatusOrdemServico.AGUARDANDO_APROVACAO, agora));
        return new OrdemServico(null, orcamentoId, atendenteId, StatusOrdemServico.AGUARDANDO_APROVACAO, agora,
                null, historico, List.of());
    }

    public static OrdemServico reconstruir(Integer id, Integer orcamentoId, Integer atendenteId,
                                           StatusOrdemServico status, LocalDateTime dataAbertura,
                                           LocalDateTime dataEntrega, List<HistoricoDeStatus> historico,
                                           List<ItemOrdemServico> itens) {
        return new OrdemServico(id, orcamentoId, atendenteId, status, dataAbertura, dataEntrega, historico, itens);
    }

    public void transicionarPara(StatusOrdemServico novoStatus, LocalDateTime agora) {
        if (!status.podeIrPara(novoStatus)) {
            throw new TransicaoStatusInvalidaException("Não é possível ir de " + status + " para " + novoStatus);
        }
        mudarPara(novoStatus, agora);
    }

    // Orçamento rejeitado encerra a ordem, sem passar pela validação do fluxo normal.
    public void finalizarPorOrcamento(LocalDateTime agora) {
        mudarPara(StatusOrdemServico.FINALIZADA, agora);
    }

    private void mudarPara(StatusOrdemServico novoStatus, LocalDateTime agora) {
        HistoricoDeStatus aberto = historico.stream()
                .filter(HistoricoDeStatus::estaAberto)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("OS sem histórico de status aberto"));
        aberto.encerrar(agora);
        historico.add(HistoricoDeStatus.abrir(novoStatus, agora));
        this.status = novoStatus;
    }

    /**
     * Ao entrar em execução a ordem herda as linhas do orçamento, mas só se ainda não tiver as suas.
     */
    public void copiarItensDoOrcamento(List<ItemOrdemServico> itensDoOrcamento) {
        if (itens.isEmpty()) {
            itens.addAll(itensDoOrcamento);
        }
    }

    public void incluirServico(Integer servicoId, int quantidade, BigDecimal valorMaoDeObra) {
        itens.add(ItemOrdemServico.novo(null, servicoId, null, quantidade, valorMaoDeObra));
    }

    public void incluirItemEstoque(Integer itemEstoqueId, int quantidade, BigDecimal valorUnitario) {
        itens.add(ItemOrdemServico.novo(null, null, itemEstoqueId, quantidade, valorUnitario));
    }

    public void exigirEmExecucao() {
        if (status != StatusOrdemServico.EM_EXECUCAO) {
            throw new OrdemServicoNaoEstaEmExecucaoException(id);
        }
    }

    public boolean estaEmExecucao() {
        return status == StatusOrdemServico.EM_EXECUCAO;
    }

    public Integer getId() {
        return id;
    }

    public Integer getOrcamentoId() {
        return orcamentoId;
    }

    public Integer getAtendenteId() {
        return atendenteId;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataEntrega() {
        return dataEntrega;
    }

    public List<HistoricoDeStatus> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public List<ItemOrdemServico> getItens() {
        return Collections.unmodifiableList(itens);
    }
}
