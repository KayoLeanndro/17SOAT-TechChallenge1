package com.kap.mechanics_api.dto.movimentacaoestoque;

import com.kap.mechanics_api.enums.TipoMovimentacaoEstoque;

import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponseDTO(
        Long id,
        Integer itemEstoqueId,
        String itemEstoqueNome,
        TipoMovimentacaoEstoque tipo,
        Integer quantidade,
        LocalDateTime dataHora,
        Integer usuarioId,
        Long ordemServicoId,
        Integer saldoItemEstoque
) {
}
