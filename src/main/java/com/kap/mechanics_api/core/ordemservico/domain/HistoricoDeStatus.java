package com.kap.mechanics_api.core.ordemservico.domain;

import java.time.LocalDateTime;

/**
 * Período em que a ordem de serviço esteve em um status. O período aberto (sem fim) é o status atual.
 */
public class HistoricoDeStatus {

    private final Long id;
    private final StatusOrdemServico status;
    private final LocalDateTime inicio;
    private LocalDateTime fim;

    private HistoricoDeStatus(Long id, StatusOrdemServico status, LocalDateTime inicio, LocalDateTime fim) {
        this.id = id;
        this.status = status;
        this.inicio = inicio;
        this.fim = fim;
    }

    static HistoricoDeStatus abrir(StatusOrdemServico status, LocalDateTime inicio) {
        return new HistoricoDeStatus(null, status, inicio, null);
    }

    public static HistoricoDeStatus reconstruir(Long id, StatusOrdemServico status, LocalDateTime inicio,
                                                LocalDateTime fim) {
        return new HistoricoDeStatus(id, status, inicio, fim);
    }

    void encerrar(LocalDateTime fim) {
        this.fim = fim;
    }

    boolean estaAberto() {
        return fim == null;
    }

    public Long getId() {
        return id;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }
}
