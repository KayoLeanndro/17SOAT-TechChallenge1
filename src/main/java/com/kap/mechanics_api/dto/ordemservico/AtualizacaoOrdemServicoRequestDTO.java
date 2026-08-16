package com.kap.mechanics_api.dto.ordemservico;

import java.time.LocalDateTime;

public record AtualizacaoOrdemServicoRequestDTO(
        Integer usuarioAtendenteId,
        Integer statusId,
        LocalDateTime dataEntrega
) {
    public boolean temAoMenosUmCampoPreenchido() {
        return usuarioAtendenteId != null || statusId != null || dataEntrega != null;
    }
}
