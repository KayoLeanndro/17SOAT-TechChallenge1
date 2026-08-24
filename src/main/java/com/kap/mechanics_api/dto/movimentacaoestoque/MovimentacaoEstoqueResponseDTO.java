package com.kap.mechanics_api.dto.movimentacaoestoque;

import com.kap.mechanics_api.enums.TipoMovimentacaoEstoque;

import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponseDTO(
        Integer id,
        Integer itemEstoqueId,
        String itemEstoqueNome,
        TipoMovimentacaoEstoque tipo,
        Integer quantidade,
        LocalDateTime dataHora,
        Integer usuarioId,
        Integer ordemServicoId,
        Integer saldoItemEstoque
) {
}
