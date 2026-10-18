package com.kap.mechanics_api.orcamento;

import com.kap.mechanics_api.adapter.persistence.entity.OrcamentoServicoJpaId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class OrcamentoServicoJpaIdTest {

    @Test
    void deveSerIguaisQuandoTodosOsCamposForemIguais() {
        OrcamentoServicoJpaId or = new OrcamentoServicoJpaId(1,1);
        or.setOrcamentoId(1);
        or.setServicoId(1);
        OrcamentoServicoJpaId or2 = new OrcamentoServicoJpaId(1,1);
        or2.setOrcamentoId(1);
        or2.setServicoId(1);

        assertEquals(or,or2);
        assertEquals(or.getOrcamentoId(),or2.getOrcamentoId());
        assertEquals(or.getServicoId(),or2.getServicoId());
        assertEquals(or.hashCode(),or2.hashCode());
    }

    @Test
    void deveSerIguaisQuandoCamposDiferirem(){
        OrcamentoServicoJpaId or = new OrcamentoServicoJpaId(1,1);
        OrcamentoServicoJpaId or2 = new OrcamentoServicoJpaId(2,2);

        assertNotEquals(or, or2);
    }
}
