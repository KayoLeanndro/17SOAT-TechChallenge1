package com.kap.mechanics_api.servico;

import com.kap.mechanics_api.domain.ClienteVeiculoId;
import com.kap.mechanics_api.domain.ServicoItemId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class ServicoItemIdTest {

    @Test
    void deveSerIguaisQuandoTodosOsCamposForemIguais() {
        ServicoItemId or = new ServicoItemId(1,1);
        or.setItemEstoqueId(1);
        or.setServicoId(1);
        ServicoItemId or2 = new ServicoItemId(1,1);
        or2.setItemEstoqueId(1);
        or2.setServicoId(1);

        assertEquals(or,or2);
        assertEquals(or.getItemEstoqueId(),or2.getItemEstoqueId());
        assertEquals(or.getServicoId(),or2.getServicoId());
        assertEquals(or.hashCode(),or2.hashCode());
    }

    @Test
    void deveSerIguaisQuandoCamposDiferirem(){
        ServicoItemId or = new ServicoItemId(1,1);
        or.setItemEstoqueId(1);
        or.setServicoId(1);
        ServicoItemId or2 = new ServicoItemId(2,2);
        or2.setItemEstoqueId(2);
        or2.setServicoId(2);

        assertNotEquals(or, or2);
    }
}
