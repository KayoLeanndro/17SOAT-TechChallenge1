package com.kap.mechanics_api.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.kap.mechanics_api.adapter.persistence.entity.ClienteJpaEntity;
import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;

class ClienteJpaMapperTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 7, 10, 30);

    private final ClienteJpaMapper mapper = new ClienteJpaMapper();

    @Test
    void deveConverterEntityParaDominio() {

        ClienteJpaEntity entity = new ClienteJpaEntity("Maria", "12345678909", "11999990000", "maria@email.com", DATA);
        entity.setId(7);

        Cliente cliente = mapper.paraDominio(entity);

        assertEquals(7, cliente.getId());
        assertEquals("Maria", cliente.getNome());
        assertEquals(new CpfCnpj("12345678909"), cliente.getCpfCnpj());
        assertEquals("11999990000", cliente.getTelefone());
        assertEquals("maria@email.com", cliente.getEmail());
        assertEquals(DATA, cliente.getDataCriacao());
    }

    @Test
    void deveConverterDominioParaEntityMantendoId() {

        Cliente cliente = Cliente.reconstruir(7, "Maria", new CpfCnpj("123.456.789-09"),
                "11999990000", "maria@email.com", DATA);

        ClienteJpaEntity entity = mapper.paraEntity(cliente);

        assertEquals(7, entity.getId());
        assertEquals("Maria", entity.getNome());
        assertEquals("12345678909", entity.getCpfCnpj());
        assertEquals("11999990000", entity.getTelefone());
        assertEquals("maria@email.com", entity.getEmail());
        assertEquals(DATA, entity.getDataCriacao());
    }

    @Test
    void deveConverterClienteNovoParaEntitySemId() {

        Cliente cliente = Cliente.novo("Maria", new CpfCnpj("12345678909"), null, null, DATA);

        ClienteJpaEntity entity = mapper.paraEntity(cliente);

        assertNull(entity.getId());
    }
}
