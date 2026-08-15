package com.kap.mechanics_api.orcamento;

import com.kap.mechanics_api.domain.*;
import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.ServicoPecaRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrcamentoServiceTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private VeiculoService veiculoService;

    @Mock
    private ServicoService servicoService;

    @Mock
    private ServicoPecaRepository servicoPecaRepository;

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
    void deveGerarOrcamentoComUmServicoSemPecas() {
        // arrange
        Servico servico = new Servico();
        servico.setId(10);
        servico.setValorMaoDeObra(new BigDecimal("150.00"));

        GeracaoOrcamentoRequestDTO dto = new GeracaoOrcamentoRequestDTO(
                cliente.getId(), veiculo.getId(), List.of(10), null
        );

        when(clienteService.pesquisarPorId(dto.clienteId())).thenReturn(cliente);
        when(veiculoService.pesquisarPorId(dto.veiculoId())).thenReturn(veiculo);
        when(servicoService.pesquisarPorId(10)).thenReturn(servico);
        when(servicoPecaRepository.findByServico_Id(10)).thenReturn(List.of());

        // simula o JPA atribuindo um id ao salvar
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(invocation -> {
            Orcamento o = invocation.getArgument(0);
            o.setId(100);
            return o;
        });

        // act
        orcamentoService.gerarOrcamento(dto);

        // assert
        ArgumentCaptor<Orcamento> orcamentoCaptor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoRepository, times(2)).save(orcamentoCaptor.capture());

        Orcamento orcamentoSalvo = orcamentoCaptor.getValue();
        assertEquals(cliente, orcamentoSalvo.getCliente());
        assertEquals(veiculo, orcamentoSalvo.getVeiculo());
        assertEquals(StatusOrcamento.PENDENTE, orcamentoSalvo.getStatusOrcamento());
        assertEquals(0, new BigDecimal("150.00").compareTo(orcamentoSalvo.getValorTotal()));

        ArgumentCaptor<OrcamentoServico> osCaptor = ArgumentCaptor.forClass(OrcamentoServico.class);
        verify(orcamentoServicoRepository, times(1)).save(osCaptor.capture());
        assertEquals(0, new BigDecimal("150.00").compareTo(osCaptor.getValue().getValorCobrado()));
    }

    @Test
    void deveCalcularValorDoServicoSomandoMaoDeObraEPecas() {
        // arrange
        Servico servico = new Servico();
        servico.setId(20);
        servico.setValorMaoDeObra(new BigDecimal("100.00"));

        Peca peca = new Peca();
        peca.setId(5);
        peca.setValorUnitario(new BigDecimal("30.00"));

        ServicoPeca servicoPeca = new ServicoPeca(servico, peca, 2); // 2 unidades

        GeracaoOrcamentoRequestDTO dto = new GeracaoOrcamentoRequestDTO(
                cliente.getId(), veiculo.getId(), List.of(20), null
        );

        when(clienteService.pesquisarPorId(dto.clienteId())).thenReturn(cliente);
        when(veiculoService.pesquisarPorId(dto.veiculoId())).thenReturn(veiculo);
        when(servicoService.pesquisarPorId(20)).thenReturn(servico);
        when(servicoPecaRepository.findByServico_Id(20)).thenReturn(List.of(servicoPeca));

        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(invocation -> {
            Orcamento o = invocation.getArgument(0);
            o.setId(101);
            return o;
        });

        // act
        orcamentoService.gerarOrcamento(dto);

        // assert
        ArgumentCaptor<OrcamentoServico> osCaptor = ArgumentCaptor.forClass(OrcamentoServico.class);
        verify(orcamentoServicoRepository).save(osCaptor.capture());
        assertEquals(0, new BigDecimal("160.00").compareTo(osCaptor.getValue().getValorCobrado()));

        ArgumentCaptor<Orcamento> orcamentoCaptor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoRepository, times(2)).save(orcamentoCaptor.capture());
        assertEquals(0, new BigDecimal("160.00").compareTo(orcamentoCaptor.getValue().getValorTotal()));
    }

    @Test
    void deveSalvarValorIndividualDeCadaServicoEnaoOTotalAgregado() {

        Servico servicoA = new Servico();
        servicoA.setId(1);
        servicoA.setValorMaoDeObra(new BigDecimal("100.00"));

        Servico servicoB = new Servico();
        servicoB.setId(2);
        servicoB.setValorMaoDeObra(new BigDecimal("50.00"));

        GeracaoOrcamentoRequestDTO dto = new GeracaoOrcamentoRequestDTO(
                cliente.getId(), veiculo.getId(), List.of(1, 2), null
        );

        when(clienteService.pesquisarPorId(dto.clienteId())).thenReturn(cliente);
        when(veiculoService.pesquisarPorId(dto.veiculoId())).thenReturn(veiculo);
        when(servicoService.pesquisarPorId(1)).thenReturn(servicoA);
        when(servicoService.pesquisarPorId(2)).thenReturn(servicoB);
        when(servicoPecaRepository.findByServico_Id(1)).thenReturn(List.of());
        when(servicoPecaRepository.findByServico_Id(2)).thenReturn(List.of());

        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(invocation -> {
            Orcamento o = invocation.getArgument(0);
            o.setId(102);
            return o;
        });

        // act
        orcamentoService.gerarOrcamento(dto);

        // assert
        ArgumentCaptor<OrcamentoServico> osCaptor = ArgumentCaptor.forClass(OrcamentoServico.class);
        verify(orcamentoServicoRepository, times(2)).save(osCaptor.capture());

        List<OrcamentoServico> salvos = osCaptor.getAllValues();

        assertEquals(0, new BigDecimal("100.00").compareTo(salvos.get(0).getValorCobrado()));
        assertEquals(0, new BigDecimal("50.00").compareTo(salvos.get(1).getValorCobrado()));
    }

    @Test
    void devePropagarExcecaoQuandoClienteNaoExistir() {
        // arrange
        GeracaoOrcamentoRequestDTO dto = new GeracaoOrcamentoRequestDTO(999, veiculo.getId(), List.of(1), null);
        when(clienteService.pesquisarPorId(999)).thenThrow(new RuntimeException("Cliente não encontrado"));

        // act e assert
        assertThrows(RuntimeException.class, () -> orcamentoService.gerarOrcamento(dto));
        verifyNoInteractions(orcamentoRepository);
    }

    @Test
    void devePropagarExcecaoQuandoVeiculoNaoExistir(){

        GeracaoOrcamentoRequestDTO dto = new GeracaoOrcamentoRequestDTO(999, veiculo.getId(), List.of(1), null);
        when(veiculoService.pesquisarPorId(999)).thenThrow(new RuntimeException("Veiculo não encontrado"));

        assertThrows(RuntimeException.class, () -> orcamentoService.gerarOrcamento(dto));
        verifyNoInteractions(orcamentoRepository);
    }
}
