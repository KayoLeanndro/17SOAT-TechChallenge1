package com.kap.mechanics_api.cliente;

import com.kap.mechanics_api.domain.ClienteVeiculoId;
import com.kap.mechanics_api.domain.OrcamentoServicoId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class ClienteVeiculoIdTest {

    @Test
    void deveSerIguaisQuandoTodosOsCamposForemIguais() {
        ClienteVeiculoId or = new ClienteVeiculoId(1,1);
        or.setClienteId(1);
        or.setVeiculoId(1);
        ClienteVeiculoId or2 = new ClienteVeiculoId();
        or2.setClienteId(1);
        or2.setVeiculoId(1);

        assertEquals(or,or2);
        assertEquals(or.getClienteId(),or2.getClienteId());
        assertEquals(or.getVeiculoId(), or2.getVeiculoId());
        assertEquals(or.hashCode(),or2.hashCode());
    }

    @Test
    void deveSerIguaisQuandoCamposDiferirem(){
        ClienteVeiculoId or = new ClienteVeiculoId(1,1);
        ClienteVeiculoId or2 = new ClienteVeiculoId(2,2);

        assertNotEquals(or, or2);
    }
}
