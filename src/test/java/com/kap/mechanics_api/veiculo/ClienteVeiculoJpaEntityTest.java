package com.kap.mechanics_api.veiculo;

import com.kap.mechanics_api.adapter.persistence.entity.ClienteJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.ClienteVeiculoJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.ClienteVeiculoJpaId;
import com.kap.mechanics_api.adapter.persistence.entity.VeiculoJpaEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteVeiculoJpaEntityTest {

    @Test
    void construtorPadraoDeveIniciarComTodosOsCamposNulos() {
        ClienteVeiculoJpaEntity clienteVeiculo = new ClienteVeiculoJpaEntity();

        assertNull(clienteVeiculo.getId());
        assertNull(clienteVeiculo.getCliente());
        assertNull(clienteVeiculo.getVeiculo());
    }

    @Test
    void construtorComArgumentosDevePreencherVeiculoECliente() {
        VeiculoJpaEntity veiculo = new VeiculoJpaEntity();
        ClienteJpaEntity clienteJpaEntity = new ClienteJpaEntity();

        ClienteVeiculoJpaEntity clienteVeiculo = new ClienteVeiculoJpaEntity(veiculo, clienteJpaEntity);

        assertEquals(veiculo, clienteVeiculo.getVeiculo());
        assertEquals(clienteJpaEntity, clienteVeiculo.getCliente());
    }

    @Test
    void construtorComArgumentosNaoDeveDefinirIdAutomaticamente() {
        // O id é um @EmbeddedId com @MapsId, então só é populado pelo
        // Hibernate no momento do persist, não pelo construtor Java puro.
        ClienteVeiculoJpaEntity clienteVeiculo = new ClienteVeiculoJpaEntity(new VeiculoJpaEntity(), new ClienteJpaEntity());

        assertNull(clienteVeiculo.getId());
    }

    @Test
    void setIdDeveAtualizarIdCorretamente() {
        ClienteVeiculoJpaEntity clienteVeiculo = new ClienteVeiculoJpaEntity();
        ClienteVeiculoJpaId id = new ClienteVeiculoJpaId(1, 10);

        clienteVeiculo.setId(id);

        assertEquals(id, clienteVeiculo.getId());
    }

    @Test
    void setClienteDeveAtualizarClienteCorretamente() {
        ClienteVeiculoJpaEntity clienteVeiculo = new ClienteVeiculoJpaEntity();
        ClienteJpaEntity clienteJpaEntity = new ClienteJpaEntity();
        clienteJpaEntity.setId(3);

        clienteVeiculo.setCliente(clienteJpaEntity);

        assertEquals(clienteJpaEntity, clienteVeiculo.getCliente());
        assertEquals(3, clienteVeiculo.getCliente().getId());
    }

    @Test
    void setVeiculoDeveAtualizarVeiculoCorretamente() {
        ClienteVeiculoJpaEntity clienteVeiculo = new ClienteVeiculoJpaEntity();
        VeiculoJpaEntity veiculo = new VeiculoJpaEntity();
        veiculo.setId(8);

        clienteVeiculo.setVeiculo(veiculo);

        assertEquals(veiculo, clienteVeiculo.getVeiculo());
        assertEquals(8, clienteVeiculo.getVeiculo().getId());
    }

    @Test
    void setClienteDevePermitirValorNulo() {
        ClienteVeiculoJpaEntity clienteVeiculo = new ClienteVeiculoJpaEntity(new VeiculoJpaEntity(), new ClienteJpaEntity());

        clienteVeiculo.setCliente(null);

        assertNull(clienteVeiculo.getCliente());
    }

    @Test
    void setVeiculoDevePermitirValorNulo() {
        ClienteVeiculoJpaEntity clienteVeiculo = new ClienteVeiculoJpaEntity(new VeiculoJpaEntity(), new ClienteJpaEntity());

        clienteVeiculo.setVeiculo(null);

        assertNull(clienteVeiculo.getVeiculo());
    }

}
