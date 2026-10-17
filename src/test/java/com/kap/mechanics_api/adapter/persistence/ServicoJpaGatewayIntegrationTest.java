package com.kap.mechanics_api.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kap.mechanics_api.core.servico.domain.Servico;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServicoJpaGatewayIntegrationTest {

    @Autowired
    private ServicoJpaGateway gateway;
    @Autowired
    private EntityManager entityManager;

    private void sincronizarComBanco() {
        entityManager.flush();
        entityManager.clear();
    }

    private Servico novo(String nome) {
        return Servico.novo(nome, "Descrição " + nome, new BigDecimal("100.00"), 60, true);
    }

    @Test
    void deveSalvarGerandoIdEPreservarTodosOsCamposNaIdaEVolta() {
        Integer id = gateway.salvar(novo("Alinhamento")).getId();
        sincronizarComBanco();

        Servico lido = gateway.pesquisarPorId(id).orElseThrow();

        assertThat(id).isNotNull();
        assertThat(lido.getNome()).isEqualTo("Alinhamento");
        assertThat(lido.getDescricao()).isEqualTo("Descrição Alinhamento");
        assertThat(lido.getValorMaoDeObra()).isEqualByComparingTo("100.00");
        assertThat(lido.getTempoEstimadoMin()).isEqualTo(60);
        assertThat(lido.isAtivo()).isTrue();
    }

    @Test
    void deveAtualizarServicoExistenteSemCriarOutro() {
        Integer id = gateway.salvar(novo("Alinhamento")).getId();
        sincronizarComBanco();
        int antes = gateway.listarTodos().size();

        Servico carregado = gateway.pesquisarPorId(id).orElseThrow();
        carregado.atualizar("Alinhamento premium", null, new BigDecimal("150.00"), null, false);
        gateway.salvar(carregado);
        sincronizarComBanco();

        Servico lido = gateway.pesquisarPorId(id).orElseThrow();
        assertThat(lido.getNome()).isEqualTo("Alinhamento premium");
        assertThat(lido.getValorMaoDeObra()).isEqualByComparingTo("150.00");
        assertThat(lido.isAtivo()).isFalse();
        assertThat(gateway.listarTodos()).hasSize(antes);
    }

    @Test
    void deveListarServicosSalvos() {
        gateway.salvar(novo("Alinhamento"));
        gateway.salvar(novo("Balanceamento"));
        sincronizarComBanco();

        assertThat(gateway.listarTodos()).extracting(Servico::getNome).contains("Alinhamento", "Balanceamento");
    }

    @Test
    void deveRetornarVazioQuandoIdNaoExiste() {
        assertThat(gateway.pesquisarPorId(999999)).isEmpty();
    }

    @Test
    void deveExcluirServico() {
        Integer id = gateway.salvar(novo("Alinhamento")).getId();
        sincronizarComBanco();

        gateway.excluir(id);
        sincronizarComBanco();

        assertThat(gateway.pesquisarPorId(id)).isEmpty();
    }
}
