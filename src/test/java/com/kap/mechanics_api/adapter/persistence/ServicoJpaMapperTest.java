package com.kap.mechanics_api.adapter.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.kap.mechanics_api.adapter.persistence.entity.ServicoJpaEntity;
import com.kap.mechanics_api.core.servico.domain.Servico;

class ServicoJpaMapperTest {

    private final ServicoJpaMapper mapper = new ServicoJpaMapper();

    @Test
    void deveConverterEntityParaDominio() {
        ServicoJpaEntity entity = new ServicoJpaEntity("Alinhamento", "3D", new BigDecimal("100.00"), 60, true);
        entity.setId(7);

        Servico servico = mapper.paraDominio(entity);

        assertEquals(7, servico.getId());
        assertEquals("Alinhamento", servico.getNome());
        assertEquals("3D", servico.getDescricao());
        assertEquals(new BigDecimal("100.00"), servico.getValorMaoDeObra());
        assertEquals(60, servico.getTempoEstimadoMin());
        assertTrue(servico.isAtivo());
    }

    @Test
    void deveConverterDominioParaEntity() {
        Servico servico = Servico.reconstruir(7, "Alinhamento", "3D", new BigDecimal("100.00"), 60, false);

        ServicoJpaEntity entity = mapper.paraEntity(servico);

        assertEquals(7, entity.getId());
        assertEquals("Alinhamento", entity.getNome());
        assertEquals("3D", entity.getDescricao());
        assertEquals(new BigDecimal("100.00"), entity.getValorMaoDeObra());
        assertEquals(60, entity.getTempoEstimadoMin());
        assertFalse(entity.isAtivo());
    }

    @Test
    void deveManterIdNuloParaServicoNovo() {
        assertNull(mapper.paraEntity(Servico.novo("A", "d", BigDecimal.ONE, 1, true)).getId());
    }
}
