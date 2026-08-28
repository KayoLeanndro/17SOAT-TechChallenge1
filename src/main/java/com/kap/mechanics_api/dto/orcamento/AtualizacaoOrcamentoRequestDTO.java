package com.kap.mechanics_api.dto.orcamento;

import com.kap.mechanics_api.enums.StatusOrcamento;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AtualizacaoOrcamentoRequestDTO(

        @NotNull
        Integer orcamentoId,
        StatusOrcamento statusOrcamento,
        List<Integer> pecaId

) {

    public boolean peloMenosUmCampoInformadoParaAtualizacao(){
        return orcamentoId != null || statusOrcamento != null || (pecaId != null && !pecaId.isEmpty());
    }

}
