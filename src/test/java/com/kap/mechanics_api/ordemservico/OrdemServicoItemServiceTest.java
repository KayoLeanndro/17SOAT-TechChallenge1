package com.kap.mechanics_api.ordemservico;

import com.kap.mechanics_api.domain.ItemEstoque;
import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrcamentoItem;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.OrdemServicoItem;
import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.domain.ServicoItem;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.dto.ordemservico.ConsultaOrdemServicoItensResponseDTO;
import com.kap.mechanics_api.dto.ordemservico.InclusaoOrdemServicoItemRequestDTO;
import com.kap.mechanics_api.enums.TipoItemEstoque;
import com.kap.mechanics_api.exception.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.exception.OrdemServicoNaoEstaEmExecucaoException;
import com.kap.mechanics_api.repository.OrcamentoItemRepository;
import com.kap.mechanics_api.repository.OrdemServicoItemRepository;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import com.kap.mechanics_api.service.ItemEstoqueService;
import com.kap.mechanics_api.service.MovimentacaoEstoqueService;
import com.kap.mechanics_api.service.OrdemServicoItemService;
import com.kap.mechanics_api.service.ServicoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoItemServiceTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private OrdemServicoItemRepository itemRepository;
    @Mock private OrcamentoItemRepository orcamentoItemRepository;
    @Mock private ServicoService servicoService;
    @Mock private ItemEstoqueService itemEstoqueService;
    @Mock private ServicoItemRepository servicoItemRepository;
    @Mock private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @InjectMocks private OrdemServicoItemService service;

    // ----- copiarDoOrcamento -----

    @Test
    void naoDeveCopiarQuandoOrdemServicoJaPossuiItens() {
        OrdemServico os = ordemServico(1, "EM_EXECUCAO");
        when(itemRepository.findByOrdemServico_Id(1)).thenReturn(List.of(new OrdemServicoItem()));

        service.copiarDoOrcamento(os);

        verifyNoInteractions(orcamentoItemRepository);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void deveCopiarTodosOsItensDoOrcamento() {
        OrdemServico os = ordemServico(1, "EM_EXECUCAO");
        when(itemRepository.findByOrdemServico_Id(1)).thenReturn(List.of());
        when(orcamentoItemRepository.findByOrcamento_Id(99)).thenReturn(List.of(
                orcamentoItem(servico(10, new BigDecimal("120.00")), null, 1, new BigDecimal("120.00")),
                orcamentoItem(null, itemEstoque(5, new BigDecimal("30.00")), 2, new BigDecimal("30.00"))
        ));

        service.copiarDoOrcamento(os);

        verify(itemRepository, times(2)).save(any(OrdemServicoItem.class));
    }

    // ----- incluir -----

    @Test
    void deveLancarExcecaoQuandoOrdemServicoNaoExiste() {
        when(ordemServicoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class,
                () -> service.incluir(1, new InclusaoOrdemServicoItemRequestDTO(null, 5, 1)));
    }

    @Test
    void deveLancarExcecaoQuandoOrdemServicoNaoEstaEmExecucao() {
        when(ordemServicoRepository.findById(1)).thenReturn(Optional.of(ordemServico(1, "RECEBIDA")));

        assertThrows(OrdemServicoNaoEstaEmExecucaoException.class,
                () -> service.incluir(1, new InclusaoOrdemServicoItemRequestDTO(null, 5, 1)));
    }

    @Test
    void deveExigirExatamenteUmaReferenciaQuandoNenhumaInformada() {
        when(ordemServicoRepository.findById(1)).thenReturn(Optional.of(ordemServico(1, "EM_EXECUCAO")));

        assertThrows(IllegalArgumentException.class,
                () -> service.incluir(1, new InclusaoOrdemServicoItemRequestDTO(null, null, 1)));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void deveExigirExatamenteUmaReferenciaQuandoAmbasInformadas() {
        when(ordemServicoRepository.findById(1)).thenReturn(Optional.of(ordemServico(1, "EM_EXECUCAO")));

        assertThrows(IllegalArgumentException.class,
                () -> service.incluir(1, new InclusaoOrdemServicoItemRequestDTO(10, 5, 1)));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void deveIncluirItemDeEstoqueEBaixarMovimentacao() {
        OrdemServico os = ordemServico(1, "EM_EXECUCAO");
        ItemEstoque estoque = itemEstoque(5, new BigDecimal("20.00"));
        when(ordemServicoRepository.findById(1)).thenReturn(Optional.of(os));
        when(itemEstoqueService.pesquisarPorId(5)).thenReturn(estoque);

        service.incluir(1, new InclusaoOrdemServicoItemRequestDTO(null, 5, 4));

        verify(itemRepository, times(1)).save(any(OrdemServicoItem.class));
        verify(movimentacaoEstoqueService).baixarItemParaOrdemServico(os, 5, 4);
    }

    @Test
    void deveIncluirServicoEExplodirComponentesDeEstoque() {
        OrdemServico os = ordemServico(1, "EM_EXECUCAO");
        Servico servico = servico(10, new BigDecimal("150.00"));
        ItemEstoque componente = itemEstoque(7, new BigDecimal("12.00"));
        when(ordemServicoRepository.findById(1)).thenReturn(Optional.of(os));
        when(servicoService.pesquisarPorId(10)).thenReturn(servico);
        when(servicoItemRepository.findByServico_Id(10)).thenReturn(List.of(servicoItem(servico, componente, 3)));

        service.incluir(1, new InclusaoOrdemServicoItemRequestDTO(10, null, 2));

        // 1 linha do serviço + 1 linha do componente de estoque
        verify(itemRepository, times(2)).save(any(OrdemServicoItem.class));
        // quantidade do componente = 2 (OS) * 3 (padrão)
        verify(movimentacaoEstoqueService).baixarItemParaOrdemServico(os, 7, 6);
    }

    // ----- consultar -----

    @Test
    void deveLancarExcecaoAoConsultarOrdemServicoInexistente() {
        when(ordemServicoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> service.consultar(1));
    }

    @Test
    void deveConsultarItensComTotalCalculado() {
        OrdemServico os = ordemServico(1, "EM_EXECUCAO");
        when(ordemServicoRepository.findById(1)).thenReturn(Optional.of(os));
        when(itemRepository.findByOrdemServico_Id(1)).thenReturn(List.of(
                ordemServicoItem(1, null, itemEstoque(5, null), "Filtro", 2, new BigDecimal("25.00")),
                ordemServicoItem(2, servico(10, null), null, "Troca de óleo", 1, new BigDecimal("100.00"))
        ));

        ConsultaOrdemServicoItensResponseDTO resposta = service.consultar(1);

        assertEquals(1, resposta.ordemServicoId());
        assertEquals(99, resposta.orcamentoId());
        assertEquals("EM_EXECUCAO", resposta.status());
        assertEquals(2, resposta.itens().size());
        assertEquals(0, new BigDecimal("150.00").compareTo(resposta.valorTotal()));
        assertEquals("ESTOQUE", resposta.itens().get(0).tipo());
        assertEquals("SERVICO", resposta.itens().get(1).tipo());
    }

    // ----- baixarItensEstoque -----

    @Test
    void deveBaixarApenasItensDeEstoqueDaOrdemServico() {
        OrdemServico os = ordemServico(1, "EM_EXECUCAO");
        when(itemRepository.findByOrdemServico_Id(1)).thenReturn(List.of(
                ordemServicoItem(1, null, itemEstoque(5, new BigDecimal("10.00")), "Filtro", 3, new BigDecimal("10.00")),
                ordemServicoItem(2, servico(10, new BigDecimal("80.00")), null, "Mão de obra", 1, new BigDecimal("80.00"))
        ));

        service.baixarItensEstoque(os);

        verify(movimentacaoEstoqueService, times(1)).baixarItemParaOrdemServico(os, 5, 3);
    }

    // ----- fábricas -----

    private OrdemServico ordemServico(Integer id, String status) {
        OrdemServico os = new OrdemServico();
        os.setId(id);
        Orcamento orcamento = new Orcamento();
        orcamento.setId(99);
        os.setOrcamento(orcamento);
        StatusOrdemServico statusOs = new StatusOrdemServico();
        statusOs.setNome(status);
        os.setStatusOrdemServico(statusOs);
        return os;
    }

    private Servico servico(Integer id, BigDecimal valorMaoDeObra) {
        Servico servico = new Servico();
        servico.setId(id);
        servico.setNome("Servico " + id);
        servico.setValorMaoDeObra(valorMaoDeObra);
        return servico;
    }

    private ItemEstoque itemEstoque(Integer id, BigDecimal valorUnitario) {
        ItemEstoque item = new ItemEstoque();
        item.setId(id);
        item.setNome("Item " + id);
        item.setTipoItemEstoque(TipoItemEstoque.PECA);
        item.setValorUnitario(valorUnitario);
        item.setAtivo(true);
        return item;
    }

    private ServicoItem servicoItem(Servico servico, ItemEstoque item, Integer quantidadePadrao) {
        ServicoItem servicoItem = new ServicoItem();
        servicoItem.setServico(servico);
        servicoItem.setItemEstoque(item);
        servicoItem.setQuantidadePadrao(quantidadePadrao);
        return servicoItem;
    }

    private OrcamentoItem orcamentoItem(Servico servico, ItemEstoque item, Integer quantidade, BigDecimal valor) {
        OrcamentoItem linha = new OrcamentoItem();
        linha.setServico(servico);
        linha.setItemEstoque(item);
        linha.setQuantidade(quantidade);
        linha.setValorUnitarioCobrado(valor);
        return linha;
    }

    private OrdemServicoItem ordemServicoItem(Integer id, Servico servico, ItemEstoque item, String nome,
                                              Integer quantidade, BigDecimal valor) {
        OrdemServicoItem linha = new OrdemServicoItem();
        linha.setId(id);
        linha.setServico(servico);
        linha.setItemEstoque(item);
        if (item != null) {
            item.setNome(nome);
        }
        if (servico != null) {
            servico.setNome(nome);
        }
        linha.setQuantidade(quantidade);
        linha.setValorUnitarioCobrado(valor);
        return linha;
    }
}
