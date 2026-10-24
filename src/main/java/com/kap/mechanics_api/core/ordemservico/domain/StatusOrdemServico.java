package com.kap.mechanics_api.core.ordemservico.domain;

import java.util.Set;

public enum StatusOrdemServico {

    RECEBIDA,
    EM_DIAGNOSTICO,
    AGUARDANDO_APROVACAO,
    EM_EXECUCAO,
    FINALIZADA,
    ENTREGUE;

    // O fluxo de status é regra da oficina, por isso mora no próprio enum e não no service.
    public Set<StatusOrdemServico> transicoesValidas() {
        return switch (this) {
            case RECEBIDA -> Set.of(EM_DIAGNOSTICO);
            case EM_DIAGNOSTICO -> Set.of(EM_EXECUCAO);
            case AGUARDANDO_APROVACAO -> Set.of(EM_DIAGNOSTICO);
            case EM_EXECUCAO -> Set.of(FINALIZADA);
            case FINALIZADA -> Set.of(ENTREGUE);
            case ENTREGUE -> Set.of();
        };
    }

    public boolean podeIrPara(StatusOrdemServico destino) {
        return transicoesValidas().contains(destino);
    }
}
