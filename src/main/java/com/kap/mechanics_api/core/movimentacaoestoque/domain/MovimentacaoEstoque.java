package com.kap.mechanics_api.core.movimentacaoestoque.domain;

import java.time.LocalDateTime;

/**
 * Lançamento do livro-razão de estoque. Referencia item, usuário e ordem de serviço apenas pelo id:
 * cada um é outro agregado e é carregado pelo gateway quando o caso de uso precisa.
 */
public class MovimentacaoEstoque {

    private final Integer id;
    private final Integer itemEstoqueId;
    private final TipoMovimentacaoEstoque tipo;
    private final Integer quantidade;
    private final LocalDateTime dataHora;
    private final Integer usuarioId;
    private final Integer ordemServicoId;

    private MovimentacaoEstoque(Integer id, Integer itemEstoqueId, TipoMovimentacaoEstoque tipo, Integer quantidade,
                                LocalDateTime dataHora, Integer usuarioId, Integer ordemServicoId) {
        if (itemEstoqueId == null) {
            throw new IllegalArgumentException("O item de estoque é um campo obrigatório");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("O tipo da movimentação é um campo obrigatório");
        }

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }

        if (dataHora == null) {
            throw new IllegalArgumentException("A data e hora da movimentação são obrigatórias");
        }

        if (usuarioId == null) {
            throw new IllegalArgumentException("O usuário é um campo obrigatório");
        }

        if (tipo == TipoMovimentacaoEstoque.SAIDA && ordemServicoId == null) {
            throw new IllegalArgumentException("A saída de estoque exige uma ordem de serviço");
        }

        this.id = id;
        this.itemEstoqueId = itemEstoqueId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.usuarioId = usuarioId;
        this.ordemServicoId = ordemServicoId;
    }

    public static MovimentacaoEstoque entrada(Integer itemEstoqueId, Integer quantidade, Integer usuarioId,
                                              LocalDateTime dataHora) {
        return new MovimentacaoEstoque(null, itemEstoqueId, TipoMovimentacaoEstoque.ENTRADA, quantidade,
                dataHora, usuarioId, null);
    }

    public static MovimentacaoEstoque saida(Integer itemEstoqueId, Integer quantidade, Integer usuarioId,
                                            Integer ordemServicoId, LocalDateTime dataHora) {
        return new MovimentacaoEstoque(null, itemEstoqueId, TipoMovimentacaoEstoque.SAIDA, quantidade,
                dataHora, usuarioId, ordemServicoId);
    }

    public static MovimentacaoEstoque reconstruir(Integer id, Integer itemEstoqueId, TipoMovimentacaoEstoque tipo,
                                                  Integer quantidade, LocalDateTime dataHora, Integer usuarioId,
                                                  Integer ordemServicoId) {
        return new MovimentacaoEstoque(id, itemEstoqueId, tipo, quantidade, dataHora, usuarioId, ordemServicoId);
    }

    // Efeito da movimentação sobre o saldo do item: entrada soma, saída subtrai.
    public int variacaoDeSaldo() {
        return tipo == TipoMovimentacaoEstoque.ENTRADA ? quantidade : -quantidade;
    }

    public Integer getId() {
        return id;
    }

    public Integer getItemEstoqueId() {
        return itemEstoqueId;
    }

    public TipoMovimentacaoEstoque getTipo() {
        return tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public Integer getOrdemServicoId() {
        return ordemServicoId;
    }
}
