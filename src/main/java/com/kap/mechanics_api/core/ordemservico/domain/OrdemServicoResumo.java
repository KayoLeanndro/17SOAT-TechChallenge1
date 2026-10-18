package com.kap.mechanics_api.core.ordemservico.domain;

import java.time.LocalDateTime;

/**
 * Visão enxuta da ordem para listagens: evita carregar histórico e linhas de cada ordem.
 */
public record OrdemServicoResumo(Integer id, Integer orcamentoId, StatusOrdemServico status,
                                 LocalDateTime dataAbertura, LocalDateTime dataEntrega) {
}
