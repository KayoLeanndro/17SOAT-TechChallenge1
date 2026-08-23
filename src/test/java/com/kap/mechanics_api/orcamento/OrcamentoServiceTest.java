package com.kap.mechanics_api.orcamento;

import com.kap.mechanics_api.domain.*;
import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.TipoItemEstoque;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import com.kap.mechanics_api.service.ClienteService;
import com.kap.mechanics_api.service.OrcamentoService;
import com.kap.mechanics_api.service.ServicoService;
import com.kap.mechanics_api.service.VeiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrcamentoServiceTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private VeiculoService veiculoService;

    @Mock
    private ServicoService servicoService;

    @Mock
    private ServicoItemRepository servicoItemRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private OrcamentoServicoRepository orcamentoServicoRepository;

    @InjectMocks
    private OrcamentoService orcamentoService;

    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);

        veiculo = new Veiculo();
        veiculo.setId(1);
    }

    @Test
    void deveGerarOrcamentoComUmServicoSemItens() {
        Servico servico = servico(10, new BigDecimal("150.00"));
        GeracaoOrcamentoRequestDTO dto = new GeracaoOrcamentoRequestDTO(cliente.getId(), veiculo.getId(), List.of(10));

        when(clienteService.pesquisarPorId(dto.clienteId())).thenReturn(cliente);
        when(veiculoService.pesquisarPorId(dto.veiculoId())).thenReturn(veiculo);
        when(servicoService.pesquisarPorId(10)).thenReturn(servico);
        when(servicoItemRepository.findByServico_Id(10)).thenReturn(List.of());
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(invocation -> {
            Orcamento orcamento = invocation.getArgument(0);
            orcamento.setId(100);
            return orcamento;
        });



        orcamentoService.gerarOrcamento(dto);

        ArgumentCaptor<Orcamento> orcamentoCaptor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoRepository, times(2)).save(orcamentoCaptor.capture());
        assertEquals(0, new BigDecimal("150.00").compareTo(orcamentoCaptor.getAllValues().get(1).getValorTotal()));
        assertEquals(StatusOrcamento.PENDENTE, orcamentoCaptor.getAllValues().get(0).getStatusOrcamento());

        ArgumentCaptor<OrcamentoServico> osCaptor = ArgumentCaptor.forClass(OrcamentoServico.class);
        verify(orcamentoServicoRepository).save(osCaptor.capture());
        assertEquals(0, new BigDecimal("150.00").compareTo(osCaptor.getValue().getValorCobrado()));
    }

    @Test
    void deveSomarMaoDeObraEItensDoServico() {
        Servico servico = servico(20, new BigDecimal("100.00"));
        ItemEstoque itemEstoque = itemEstoque(5, new BigDecimal("30.00"));

        ServicoItem servicoItem = new ServicoItem();
        servicoItem.setId(new ServicoItemId(servico.getId(), itemEstoque.getId()));
        servicoItem.setServico(servico);
        servicoItem.setItemEstoque(itemEstoque);
        servicoItem.setQuantidadePadrao(2);

        GeracaoOrcamentoRequestDTO dto = new GeracaoOrcamentoRequestDTO(cliente.getId(), veiculo.getId(), List.of(20));

        when(clienteService.pesquisarPorId(dto.clienteId())).thenReturn(cliente);
        when(veiculoService.pesquisarPorId(dto.veiculoId())).thenReturn(veiculo);
        when(servicoService.pesquisarPorId(20)).thenReturn(servico);
        when(servicoItemRepository.findByServico_Id(20)).thenReturn(List.of(servicoItem));
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(invocation -> {
            Orcamento orcamento = invocation.getArgument(0);
            orcamento.setId(101);
            return orcamento;
        });

        orcamentoService.gerarOrcamento(dto);

        ArgumentCaptor<OrcamentoServico> osCaptor = ArgumentCaptor.forClass(OrcamentoServico.class);
        verify(orcamentoServicoRepository).save(osCaptor.capture());
        assertEquals(0, new BigDecimal("160.00").compareTo(osCaptor.getValue().getValorCobrado()));

        ArgumentCaptor<Orcamento> orcamentoCaptor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoRepository, times(2)).save(orcamentoCaptor.capture());
        assertEquals(0, new BigDecimal("160.00").compareTo(orcamentoCaptor.getAllValues().get(1).getValorTotal()));
    }

    @Test
    void deveSomarItensDeMaisDeUmServico() {
        Servico servicoA = servico(1, new BigDecimal("100.00"));
        Servico servicoB = servico(2, new BigDecimal("50.00"));

        GeracaoOrcamentoRequestDTO dto = new GeracaoOrcamentoRequestDTO(cliente.getId(), veiculo.getId(), List.of(1, 2));

        when(clienteService.pesquisarPorId(dto.clienteId())).thenReturn(cliente);
        when(veiculoService.pesquisarPorId(dto.veiculoId())).thenReturn(veiculo);
        when(servicoService.pesquisarPorId(1)).thenReturn(servicoA);
        when(servicoService.pesquisarPorId(2)).thenReturn(servicoB);
        when(servicoItemRepository.findByServico_Id(1)).thenReturn(List.of());
        when(servicoItemRepository.findByServico_Id(2)).thenReturn(List.of());
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(invocation -> {
            Orcamento orcamento = invocation.getArgument(0);
            orcamento.setId(102);
            return orcamento;
        });

        orcamentoService.gerarOrcamento(dto);

        ArgumentCaptor<OrcamentoServico> osCaptor = ArgumentCaptor.forClass(OrcamentoServico.class);
        verify(orcamentoServicoRepository, org.mockito.Mockito.times(2)).save(osCaptor.capture());

        List<OrcamentoServico> salvos = osCaptor.getAllValues();
        assertEquals(0, new BigDecimal("100.00").compareTo(salvos.get(0).getValorCobrado()));
        assertEquals(0, new BigDecimal("50.00").compareTo(salvos.get(1).getValorCobrado()));
    }

    private Servico servico(Integer id, BigDecimal valorMaoDeObra) {
        Servico servico = new Servico();
        servico.setId(id);
        servico.setValorMaoDeObra(valorMaoDeObra);
        return servico;
    }

    private ItemEstoque itemEstoque(Integer id, BigDecimal valorUnitario) {
        ItemEstoque itemEstoque = new ItemEstoque();
        itemEstoque.setId(id);
        itemEstoque.setNome("Item " + id);
        itemEstoque.setDescricao("Descricao " + id);
        itemEstoque.setTipoItemEstoque(TipoItemEstoque.PECA);
        itemEstoque.setValorUnitario(valorUnitario);
        itemEstoque.setQuantidadeAtual(10);
        itemEstoque.setQuantidadeMinima(2);
        itemEstoque.setAtivo(true);
        return itemEstoque;
    }
}
