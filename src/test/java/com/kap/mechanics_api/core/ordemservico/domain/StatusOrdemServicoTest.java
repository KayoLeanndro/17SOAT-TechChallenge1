package com.kap.mechanics_api.core.ordemservico.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

class StatusOrdemServicoTest {

    @ParameterizedTest
    @CsvSource({
            "RECEBIDA, EM_DIAGNOSTICO",
            "AGUARDANDO_APROVACAO, EM_DIAGNOSTICO",
            "EM_DIAGNOSTICO, EM_EXECUCAO",
            "EM_EXECUCAO, FINALIZADA",
            "FINALIZADA, ENTREGUE"
    })
    void devePermitirAsTransicoesDoFluxo(StatusOrdemServico origem, StatusOrdemServico destino) {
        assertTrue(origem.podeIrPara(destino));
    }

    @ParameterizedTest
    @CsvSource({
            "RECEBIDA, EM_EXECUCAO",
            "EM_EXECUCAO, EM_DIAGNOSTICO",
            "EM_EXECUCAO, RECEBIDA",
            "FINALIZADA, EM_EXECUCAO"
    })
    void deveBloquearPularEtapasOuVoltar(StatusOrdemServico origem, StatusOrdemServico destino) {
        assertFalse(origem.podeIrPara(destino));
    }

    @Test
    void entregueNaoTemParaOndeIr() {
        assertTrue(StatusOrdemServico.ENTREGUE.transicoesValidas().isEmpty());
    }
}
