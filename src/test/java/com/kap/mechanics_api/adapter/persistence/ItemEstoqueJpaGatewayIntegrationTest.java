package com.kap.mechanics_api.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemEstoqueJpaGatewayIntegrationTest {

    @Autowired
    private ItemEstoqueJpaGateway gateway;
    @Autowired
    private EntityManager entityManager;

    private void sincronizarComBanco() {
        entityManager.flush();
        entityManager.clear();
    }

    private ItemEstoque novo(String nome) {
        return ItemEstoque.novo(nome, "Descrição " + nome, TipoItemEstoque.PECA, new BigDecimal("25.00"), 2, true);
    }

    @Test
    void deveSalvarGerandoIdEPreservarTodosOsCamposNaIdaEVolta() {
        Integer id = gateway.salvar(novo("Filtro")).getId();
        sincronizarComBanco();

        ItemEstoque lido = gateway.pesquisarPorId(id).orElseThrow();

        assertThat(id).isNotNull();
        assertThat(lido.getNome()).isEqualTo("Filtro");
        assertThat(lido.getTipoItemEstoque()).isEqualTo(TipoItemEstoque.PECA);
        assertThat(lido.getValorUnitario()).isEqualByComparingTo("25.00");
        assertThat(lido.getQuantidadeAtual()).isZero();
        assertThat(lido.getQuantidadeMinima()).isEqualTo(2);
        assertThat(lido.isAtivo()).isTrue();
    }

    @Test
    void devePersistirOSaldoAlteradoPorMovimentacao() {
        Integer id = gateway.salvar(novo("Filtro")).getId();
        sincronizarComBanco();

        ItemEstoque carregado = gateway.pesquisarPorIdParaAtualizacao(id).orElseThrow();
        carregado.registrarEntrada(12);
        gateway.salvar(carregado);
        sincronizarComBanco();

        assertThat(gateway.pesquisarPorId(id).orElseThrow().getQuantidadeAtual()).isEqualTo(12);
    }

    @Test
    void deveAtualizarSemCriarOutroItem() {
        Integer id = gateway.salvar(novo("Filtro")).getId();
        sincronizarComBanco();
        int antes = gateway.listarTodos().size();

        ItemEstoque carregado = gateway.pesquisarPorId(id).orElseThrow();
        carregado.atualizar("Filtro novo", null, TipoItemEstoque.INSUMO, null, null, false);
        gateway.salvar(carregado);
        sincronizarComBanco();

        ItemEstoque lido = gateway.pesquisarPorId(id).orElseThrow();
        assertThat(lido.getNome()).isEqualTo("Filtro novo");
        assertThat(lido.getTipoItemEstoque()).isEqualTo(TipoItemEstoque.INSUMO);
        assertThat(lido.isAtivo()).isFalse();
        assertThat(gateway.listarTodos()).hasSize(antes);
    }

    @Test
    void deveRetornarVazioQuandoIdNaoExiste() {
        assertThat(gateway.pesquisarPorId(999999)).isEmpty();
        assertThat(gateway.pesquisarPorIdParaAtualizacao(999999)).isEmpty();
    }

    @Test
    void deveExcluirItem() {
        Integer id = gateway.salvar(novo("Filtro")).getId();
        sincronizarComBanco();

        gateway.excluir(id);
        sincronizarComBanco();

        assertThat(gateway.pesquisarPorId(id)).isEmpty();
    }
}
