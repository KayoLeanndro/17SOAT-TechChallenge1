package com.kap.mechanics_api.ordemservico;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.dto.ordemservico.ListagemOrdemServicoResponseDTO;
import com.kap.mechanics_api.exception.ClienteNaoEncontradoException;
import com.kap.mechanics_api.repository.ClienteRepository;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import com.kap.mechanics_api.repository.UsuarioRepository;
import com.kap.mechanics_api.service.OrdemServicoService;
import com.kap.mechanics_api.service.TransicaoStatusOrdemServico;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoConsultaServiceTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private StatusOrdemServicoRepository statusOrdemServicoRepository;
    @Mock private OrcamentoRepository orcamentoRepository;
    @Mock private TransicaoStatusOrdemServico transicaoStatusOrdemServico;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ClienteRepository clienteRepository;

    @InjectMocks private OrdemServicoService ordemServicoService;

    @Test
    void deveListarOrdensServicoPorIdDoCliente() {
        Cliente cliente = cliente(7, "12345678900");
        OrdemServico ordemServico = ordemServico(9, 11, "EM_EXECUCAO");
        when(clienteRepository.findById(7)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.findByOrcamento_Cliente_IdOrderByDataAberturaDesc(7))
                .thenReturn(List.of(ordemServico));

        List<ListagemOrdemServicoResponseDTO> resultado = ordemServicoService.listarPorCliente(7, null);

        assertThat(resultado).containsExactly(new ListagemOrdemServicoResponseDTO(
                9, 11, "EM_EXECUCAO", ordemServico.getDataAbertura(), null));
        verify(clienteRepository).findById(7);
        verify(ordemServicoRepository).findByOrcamento_Cliente_IdOrderByDataAberturaDesc(7);
    }

    @Test
    void deveListarOrdensServicoPorCpfFormatado() {
        Cliente cliente = cliente(7, "12345678900");
        when(clienteRepository.findByCpfCnpj("12345678900")).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.findByOrcamento_Cliente_IdOrderByDataAberturaDesc(7)).thenReturn(List.of());

        List<ListagemOrdemServicoResponseDTO> resultado = ordemServicoService.listarPorCliente(null, "123.456.789-00");

        assertThat(resultado).isEmpty();
        verify(clienteRepository).findByCpfCnpj("12345678900");
        verify(ordemServicoRepository).findByOrcamento_Cliente_IdOrderByDataAberturaDesc(7);
    }

    @Test
    void deveListarOrdensServicoPorCnpj() {
        Cliente cliente = cliente(8, "12345678000190");
        when(clienteRepository.findByCpfCnpj("12345678000190")).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.findByOrcamento_Cliente_IdOrderByDataAberturaDesc(8)).thenReturn(List.of());

        ordemServicoService.listarPorCliente(null, "12.345.678/0001-90");

        verify(clienteRepository).findByCpfCnpj("12345678000190");
    }

    @Test
    void deveLancarExcecaoQuandoClientePorIdNaoExistir() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordemServicoService.listarPorCliente(99, null))
                .isInstanceOf(ClienteNaoEncontradoException.class);

        verifyNoInteractions(ordemServicoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoClientePorDocumentoNaoExistir() {
        when(clienteRepository.findByCpfCnpj("12345678900")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordemServicoService.listarPorCliente(null, "12345678900"))
                .isInstanceOf(ClienteNaoEncontradoException.class);

        verifyNoInteractions(ordemServicoRepository);
    }

    @Test
    void deveRejeitarAusenciaDeFiltro() {
        assertThatThrownBy(() -> ordemServicoService.listarPorCliente(null, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Informe o ID do cliente ou CPF/CNPJ");
    }

    @Test
    void deveRejeitarDoisFiltrosInformados() {
        assertThatThrownBy(() -> ordemServicoService.listarPorCliente(7, "12345678900"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Informe apenas o ID do cliente ou CPF/CNPJ");
    }

    @Test
    void deveRejeitarDocumentoComCaracteresInvalidosOuQuantidadeInvalida() {
        assertThatThrownBy(() -> ordemServicoService.listarPorCliente(null, "abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O documento contém caracteres inválidos");
        assertThatThrownBy(() -> ordemServicoService.listarPorCliente(null, "123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O documento deve possuir 11 dígitos para CPF ou 14 para CNPJ");
    }

    private Cliente cliente(Integer id, String cpfCnpj) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setCpfCnpj(cpfCnpj);
        return cliente;
    }

    private OrdemServico ordemServico(Integer id, Integer orcamentoId, String status) {
        Orcamento orcamento = new Orcamento();
        orcamento.setId(orcamentoId);
        StatusOrdemServico statusOrdemServico = new StatusOrdemServico();
        statusOrdemServico.setNome(status);
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setId(id);
        ordemServico.setOrcamento(orcamento);
        ordemServico.setStatusOrdemServico(statusOrdemServico);
        ordemServico.setDataAbertura(LocalDateTime.of(2026, 8, 28, 10, 0));
        return ordemServico;
    }
}
