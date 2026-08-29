package com.kap.mechanics_api.dto.orcamento;

import com.kap.mechanics_api.enums.StatusOrcamento;
import java.math.BigDecimal;
import java.util.List;

public record ConsultaOrcamentoItensResponseDTO(
        Integer orcamentoId,
        StatusOrcamento status,
        BigDecimal valorTotal,
        List<ItemOrcamentoResponseDTO> itens
) {}
