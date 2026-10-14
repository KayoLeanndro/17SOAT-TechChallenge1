package com.kap.mechanics_api.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.kap.mechanics_api.adapter.persistence.entity.VeiculoJpaEntity;
import com.kap.mechanics_api.core.veiculo.domain.Veiculo;

class VeiculoJpaMapperTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 7, 10, 30);

    private final VeiculoJpaMapper mapper = new VeiculoJpaMapper();

    @Test
    void deveConverterEntityParaDominio() {
        VeiculoJpaEntity entity = new VeiculoJpaEntity(7, "ABC1D23", "Honda", "Civic", 2020);
        entity.setDataCriacao(DATA);

        Veiculo veiculo = mapper.paraDominio(entity);

        assertEquals(7, veiculo.getId());
        assertEquals("ABC1D23", veiculo.getPlaca());
        assertEquals("Honda", veiculo.getMarca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals(2020, veiculo.getAno());
        assertEquals(DATA, veiculo.getDataCriacao());
    }

    @Test
    void deveConverterDominioParaEntityMantendoIdEDataDoDominio() {
        Veiculo veiculo = Veiculo.reconstruir(7, "ABC1D23", "Honda", "Civic", 2020, DATA);

        VeiculoJpaEntity entity = mapper.paraEntity(veiculo);

        assertEquals(7, entity.getId());
        assertEquals("ABC1D23", entity.getPlaca());
        assertEquals("Honda", entity.getMarca());
        assertEquals("Civic", entity.getModelo());
        assertEquals(2020, entity.getAno());
        assertEquals(DATA, entity.getDataCriacao());
    }

    @Test
    void deveManterIdNuloParaVeiculoNovo() {
        Veiculo novo = Veiculo.novo("ABC1D23", "Honda", "Civic", 2020, DATA);

        assertNull(mapper.paraEntity(novo).getId());
    }
}
