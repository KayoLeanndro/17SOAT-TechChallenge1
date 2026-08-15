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
}
