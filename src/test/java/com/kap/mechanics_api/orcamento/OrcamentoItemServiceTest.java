package com.kap.mechanics_api.orcamento;

import com.kap.mechanics_api.domain.ItemEstoque;
import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.dto.orcamento.InclusaoOrcamentoItemRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.TipoItemEstoque;
import com.kap.mechanics_api.repository.OrcamentoItemRepository;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import com.kap.mechanics_api.service.ItemEstoqueService;
import com.kap.mechanics_api.service.OrcamentoItemService;
import com.kap.mechanics_api.service.ServicoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoItemServiceTest {
    @Mock private OrcamentoRepository orcamentoRepository;
    @Mock private OrcamentoItemRepository itemRepository;
    @Mock private ServicoService servicoService;
    @Mock private ItemEstoqueService itemEstoqueService;
    @Mock private ServicoItemRepository servicoItemRepository;
    @InjectMocks private OrcamentoItemService service;

    @Test
    void deveIncluirItemDeEstoqueECorrigirTotalDoOrcamento() {
        Orcamento orcamento = new Orcamento(); orcamento.setId(1); orcamento.setStatusOrcamento(StatusOrcamento.PENDENTE); orcamento.setValorTotal(new BigDecimal("100.00"));
        ItemEstoque item = new ItemEstoque("Filtro", "Filtro de óleo", TipoItemEstoque.PECA, new BigDecimal("25.50"), 3, 1, true); item.setId(2);
        when(orcamentoRepository.findById(1)).thenReturn(Optional.of(orcamento));
        when(itemEstoqueService.pesquisarPorId(2)).thenReturn(item);

        service.incluir(1, new InclusaoOrcamentoItemRequestDTO(null, 2, 2));

        assertEquals(0, new BigDecimal("151.00").compareTo(orcamento.getValorTotal()));
        ArgumentCaptor<com.kap.mechanics_api.domain.OrcamentoItem> captor = ArgumentCaptor.forClass(com.kap.mechanics_api.domain.OrcamentoItem.class);
        verify(itemRepository).save(captor.capture());
        assertEquals(item, captor.getValue().getItemEstoque());
        assertEquals(new BigDecimal("25.50"), captor.getValue().getValorUnitarioCobrado());
        verify(orcamentoRepository).save(orcamento);
    }

    @Test
    void deveImpedirItemDeOrcamentoAprovado() {
        Orcamento orcamento = new Orcamento(); orcamento.setStatusOrcamento(StatusOrcamento.APROVADO);
        when(orcamentoRepository.findById(1)).thenReturn(Optional.of(orcamento));

        assertThrows(IllegalArgumentException.class, () -> service.incluir(1, new InclusaoOrcamentoItemRequestDTO(null, 2, 1)));
        verifyNoInteractions(itemRepository, itemEstoqueService);
    }

    @Test
    void deveExigirExatamenteUmaReferencia() {
        Orcamento orcamento = new Orcamento(); orcamento.setStatusOrcamento(StatusOrcamento.PENDENTE);
        when(orcamentoRepository.findById(1)).thenReturn(Optional.of(orcamento));

        assertThrows(IllegalArgumentException.class, () -> service.incluir(1, new InclusaoOrcamentoItemRequestDTO(1, 2, 1)));
        verify(itemRepository, never()).save(any());
    }
}
