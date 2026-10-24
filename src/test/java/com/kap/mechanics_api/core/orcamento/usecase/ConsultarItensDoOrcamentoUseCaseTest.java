package com.kap.mechanics_api.core.orcamento.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;
import com.kap.mechanics_api.core.itemestoque.domain.TipoItemEstoque;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.orcamento.domain.ItemOrcamento;
import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.domain.StatusOrcamento;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.servico.domain.Servico;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;

@ExtendWith(MockitoExtension.class)
class ConsultarItensDoOrcamentoUseCaseTest {

    @Mock private OrcamentoGateway orcamentoGateway;
    @Mock private ServicoGateway servicoGateway;
    @Mock private ItemEstoqueGateway itemEstoqueGateway;

    @InjectMocks
    private ConsultarItensDoOrcamentoUseCase useCase;

    @Test
    void deveDescreverCadaLinhaComNomeTipoETotal() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.of(Orcamento.reconstruir(5, 1, 2,
                new BigDecimal("160.00"), StatusOrcamento.PENDENTE, LocalDateTime.now(), null,
                List.of(ItemOrcamento.reconstruir(1, 10, null, 1, new BigDecimal("100.00")),
                        ItemOrcamento.reconstruir(2, null, 7, 2, new BigDecimal("30.00"))),
                List.of())));
        when(servicoGateway.pesquisarPorId(10)).thenReturn(Optional.of(
                Servico.reconstruir(10, "Revisão", "Completa", new BigDecimal("100.00"), 60, true)));
        when(itemEstoqueGateway.pesquisarPorId(7)).thenReturn(Optional.of(ItemEstoque.reconstruir(
                7, "Filtro", "Filtro de óleo", TipoItemEstoque.PECA, new BigDecimal("30.00"), 10, 1, true)));

        ConsultaDeItensDoOrcamento consulta = useCase.executar(5);

        assertEquals(5, consulta.orcamentoId());
        assertEquals(StatusOrcamento.PENDENTE, consulta.status());
        assertEquals(0, new BigDecimal("160.00").compareTo(consulta.valorTotal()));
        assertEquals(2, consulta.linhas().size());
        var servico = consulta.linhas().get(0);
        assertEquals(ConsultaDeItensDoOrcamento.Tipo.SERVICO, servico.tipo());
        assertEquals("Revisão", servico.nome());
        var peca = consulta.linhas().get(1);
        assertEquals(ConsultaDeItensDoOrcamento.Tipo.ESTOQUE, peca.tipo());
        assertEquals("Filtro", peca.nome());
        assertEquals(0, new BigDecimal("60.00").compareTo(peca.valorTotal()));
    }

    @Test
    void deveFalharQuandoOrcamentoNaoExiste() {
        when(orcamentoGateway.pesquisarPorId(5)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> useCase.executar(5));
    }
}
