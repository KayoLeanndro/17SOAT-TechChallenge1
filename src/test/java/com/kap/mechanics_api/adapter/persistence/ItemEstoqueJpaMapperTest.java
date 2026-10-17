package com.kap.mechanics_api.adapter.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.kap.mechanics_api.adapter.persistence.entity.ItemEstoqueJpaEntity;
import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;

class ItemEstoqueJpaMapperTest {

    private final ItemEstoqueJpaMapper mapper = new ItemEstoqueJpaMapper();

    @Test
    void deveConverterEntityParaDominio() {
        ItemEstoqueJpaEntity entity = new ItemEstoqueJpaEntity("Filtro", "Óleo", TipoItemEstoque.INSUMO,
                new BigDecimal("25.00"), 8, 2, true);
        entity.setId(4);

        ItemEstoque item = mapper.paraDominio(entity);

        assertEquals(4, item.getId());
        assertEquals("Filtro", item.getNome());
        assertEquals("Óleo", item.getDescricao());
        assertEquals(TipoItemEstoque.INSUMO, item.getTipoItemEstoque());
        assertEquals(new BigDecimal("25.00"), item.getValorUnitario());
        assertEquals(8, item.getQuantidadeAtual());
        assertEquals(2, item.getQuantidadeMinima());
        assertTrue(item.isAtivo());
    }

    @Test
    void deveConverterDominioParaEntityPreservandoOSaldo() {
        ItemEstoque item = ItemEstoque.reconstruir(4, "Filtro", "Óleo", TipoItemEstoque.PECA,
                new BigDecimal("25.00"), 8, 2, false);

        ItemEstoqueJpaEntity entity = mapper.paraEntity(item);

        assertEquals(4, entity.getId());
        assertEquals("Filtro", entity.getNome());
        assertEquals(TipoItemEstoque.PECA, entity.getTipoItemEstoque());
        assertEquals(8, entity.getQuantidadeAtual());
        assertEquals(2, entity.getQuantidadeMinima());
        assertFalse(entity.isAtivo());
    }

    @Test
    void deveManterIdNuloParaItemNovo() {
        ItemEstoque novo = ItemEstoque.novo("Filtro", "Óleo", TipoItemEstoque.PECA, BigDecimal.TEN, 1, true);

        assertNull(mapper.paraEntity(novo).getId());
    }
}
