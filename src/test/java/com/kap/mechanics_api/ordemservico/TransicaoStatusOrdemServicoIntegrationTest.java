package com.kap.mechanics_api.ordemservico;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.domain.HistoricoStatusOs;
import com.kap.mechanics_api.domain.ItemEstoque;
import com.kap.mechanics_api.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrcamentoServico;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.domain.ServicoItem;
import com.kap.mechanics_api.domain.ServicoItemId;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.enums.TipoItemEstoque;
import com.kap.mechanics_api.enums.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.enums.TipoUsuario;
import com.kap.mechanics_api.exception.EstoqueInsuficienteException;
import com.kap.mechanics_api.exception.TransicaoStatusInvalidaException;
import com.kap.mechanics_api.repository.ClienteRepository;
import com.kap.mechanics_api.repository.HistoricoStatusOsRepository;
import com.kap.mechanics_api.repository.ItemEstoqueRepository;
import com.kap.mechanics_api.repository.MovimentacaoEstoqueRepository;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import com.kap.mechanics_api.repository.ServicoRepository;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import com.kap.mechanics_api.repository.UsuarioRepository;
import com.kap.mechanics_api.repository.VeiculoRepository;
import com.kap.mechanics_api.service.TransicaoStatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransicaoStatusOrdemServicoIntegrationTest {

    @Autowired
    private TransicaoStatusOrdemServico transicaoStatusOrdemServico;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private OrcamentoServicoRepository orcamentoServicoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ServicoItemRepository servicoItemRepository;

    @Autowired
    private ItemEstoqueRepository itemEstoqueRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Autowired
    private StatusOrdemServicoRepository statusOrdemServicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistoricoStatusOsRepository historicoStatusOsRepository;

    @BeforeEach
    void limparDadosBaseAntesDoTeste() {
        movimentacaoEstoqueRepository.deleteAll();
        historicoStatusOsRepository.deleteAll();
        ordemServicoRepository.deleteAll();
        orcamentoServicoRepository.deleteAll();
        orcamentoRepository.deleteAll();
        servicoItemRepository.deleteAll();
        itemEstoqueRepository.deleteAll();
        servicoRepository.deleteAll();
        clienteRepository.deleteAll();
        veiculoRepository.deleteAll();
        statusOrdemServicoRepository.deleteAll();
        usuarioRepository.deleteAll();
        seedStatusOrdemServico();
    }

    private void seedStatusOrdemServico() {
        for (StatusOrdemServicoEnum valor : StatusOrdemServicoEnum.values()) {
            StatusOrdemServico status = new StatusOrdemServico();
            status.setNome(valor.name());
            statusOrdemServicoRepository.save(status);
        }
    }

    private StatusOrdemServico status(StatusOrdemServicoEnum valor) {
        return statusOrdemServicoRepository.findByNome(valor.name()).orElseThrow();
    }

    private Usuario persistirUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("atendente-teste");
        usuario.setLogin("atendente-teste");
        usuario.setSenhaHash("hash");
        usuario.setTipo(TipoUsuario.ATENDENTE);
        usuario.setDataCriacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    private Orcamento persistirOrcamento() {
        Cliente cliente = clienteRepository.save(
                new Cliente("João Silva", "12345678900", "51999999999", "joao@email.com", LocalDateTime.now()));

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("Honda");
        veiculo.setModelo("Civic");
        veiculo.setAno(2020);
        veiculo.setDataCriacao(LocalDateTime.now());
        veiculo = veiculoRepository.save(veiculo);

        Orcamento orcamento = new Orcamento();
        orcamento.setCliente(cliente);
        orcamento.setVeiculo(veiculo);
        orcamento.setValorTotal(new BigDecimal("100.00"));
        orcamento.setStatusOrcamento(StatusOrcamento.APROVADO);
        orcamento.setDataCriacao(LocalDateTime.now());
        return orcamentoRepository.save(orcamento);
    }

    private OrdemServico persistirOrdemServico(Orcamento orcamento, StatusOrdemServicoEnum statusAtual) {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setOrcamento(orcamento);
        ordemServico.setUsuarioAtendente(persistirUsuario());
        ordemServico.setStatusOrdemServico(status(statusAtual));
        LocalDateTime dataAbertura = LocalDateTime.now();
        ordemServico.setDataAbertura(dataAbertura);
        OrdemServico ordemServicoSalva = ordemServicoRepository.save(ordemServico);
        historicoStatusOsRepository.save(new HistoricoStatusOs(
                ordemServicoSalva, ordemServicoSalva.getStatusOrdemServico(), dataAbertura));
        return ordemServicoSalva;
    }

    @Test
    @DisplayName("deve transicionar de RECEBIDA para EM_DIAGNOSTICO e persistir o novo status")
    void deveTransicionarDeRecebidaParaEmDiagnostico() {
        OrdemServico ordemServico =
                persistirOrdemServico(persistirOrcamento(), StatusOrdemServicoEnum.RECEBIDA);

        transicaoStatusOrdemServico.transicionar(ordemServico, StatusOrdemServicoEnum.EM_DIAGNOSTICO);
        ordemServicoRepository.save(ordemServico);

        OrdemServico recarregada = ordemServicoRepository.findById(ordemServico.getId()).orElseThrow();
        assertThat(recarregada.getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.EM_DIAGNOSTICO.name());
        assertThat(recarregada.getStatusOrdemServico().getId()).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
            "AGUARDANDO_APROVACAO, EM_DIAGNOSTICO",
            "EM_DIAGNOSTICO, EM_EXECUCAO",
            "EM_EXECUCAO, FINALIZADA",
            "FINALIZADA, ENTREGUE"
    })
    @DisplayName("deve permitir todas as transições válidas do fluxo da ordem de serviço")
    void devePermitirTodasAsTransicoesValidasDoFluxo(String statusAtual, String statusDestino) {
        OrdemServico ordemServico = persistirOrdemServico(
                persistirOrcamento(), StatusOrdemServicoEnum.valueOf(statusAtual));

        transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.valueOf(statusDestino));
        ordemServicoRepository.save(ordemServico);

        assertThat(ordemServicoRepository.findById(ordemServico.getId()).orElseThrow()
                .getStatusOrdemServico().getNome())
                .isEqualTo(statusDestino);
    }

    @Test
    @DisplayName("deve lançar TransicaoStatusInvalidaException ao pular etapas do fluxo")
    void deveLancarExcecaoAoPularEtapas() {
        OrdemServico ordemServico =
                persistirOrdemServico(persistirOrcamento(), StatusOrdemServicoEnum.RECEBIDA);

        assertThatThrownBy(() -> transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.EM_EXECUCAO))
                .isInstanceOf(TransicaoStatusInvalidaException.class)
                .hasMessageContaining("RECEBIDA")
                .hasMessageContaining("EM_EXECUCAO");

        assertThat(ordemServicoRepository.findById(ordemServico.getId()).orElseThrow()
                .getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.RECEBIDA.name());
    }

    @Test
    @DisplayName("deve lançar TransicaoStatusInvalidaException ao tentar retroceder no fluxo")
    void deveLancarExcecaoAoRetrocederNoFluxo() {
        OrdemServico ordemServico =
                persistirOrdemServico(persistirOrcamento(), StatusOrdemServicoEnum.EM_EXECUCAO);

        assertThatThrownBy(() -> transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.RECEBIDA))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
    }

    @Test
    @DisplayName("deve lançar TransicaoStatusInvalidaException ao transicionar uma OS já entregue")
    void deveLancarExcecaoAoTransicionarOrdemServicoEntregue() {
        OrdemServico ordemServico =
                persistirOrdemServico(persistirOrcamento(), StatusOrdemServicoEnum.ENTREGUE);

        assertThatThrownBy(() -> transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.FINALIZADA))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
    }

    @Test
    @DisplayName("deve dar baixa no estoque e registrar movimentação de saída ao transicionar para EM_EXECUCAO")
    void deveDarBaixaNoEstoqueAoEntrarEmExecucao() {
        Orcamento orcamento = persistirOrcamento();

        Servico servico = servicoRepository.save(
                new Servico("Troca de pastilhas", "Freios", new BigDecimal("120.00"), 90, true));

        ItemEstoque item = itemEstoqueRepository.save(new ItemEstoque(
                "Pastilha de freio", "Peca", TipoItemEstoque.PECA,
                new BigDecimal("45.00"), 10, 1, true));

        ServicoItem servicoItem = new ServicoItem();
        servicoItem.setId(new ServicoItemId(servico.getId(), item.getId()));
        servicoItem.setServico(servico);
        servicoItem.setItemEstoque(item);
        servicoItem.setQuantidadePadrao(3);
        servicoItemRepository.save(servicoItem);

        orcamentoServicoRepository.save(new OrcamentoServico(orcamento, servico, new BigDecimal("255.00")));

        OrdemServico ordemServico =
                persistirOrdemServico(orcamento, StatusOrdemServicoEnum.EM_DIAGNOSTICO);

        transicaoStatusOrdemServico.transicionar(ordemServico, StatusOrdemServicoEnum.EM_EXECUCAO);

        assertThat(itemEstoqueRepository.findById(item.getId()).orElseThrow().getQuantidadeAtual())
                .isEqualTo(7);

        List<MovimentacaoEstoque> movimentacoes = movimentacaoEstoqueRepository.findAll();
        assertThat(movimentacoes).hasSize(1);
        MovimentacaoEstoque saida = movimentacoes.get(0);
        assertThat(saida.getTipo()).isEqualTo(TipoMovimentacaoEstoque.SAIDA);
        assertThat(saida.getQuantidade()).isEqualTo(3);
        assertThat(saida.getItemEstoque().getId()).isEqualTo(item.getId());
        assertThat(saida.getOrdemServico().getId()).isEqualTo(ordemServico.getId());
    }

    @Test
    @DisplayName("deve desfazer status e histórico quando a baixa de estoque falhar")
    void deveDesfazerTransicaoQuandoEstoqueForInsuficiente() {
        Orcamento orcamento = persistirOrcamento();
        Servico servico = servicoRepository.save(
                new Servico("Troca de pastilhas", "Freios", new BigDecimal("120.00"), 90, true));
        ItemEstoque item = itemEstoqueRepository.save(new ItemEstoque(
                "Pastilha de freio", "Peca", TipoItemEstoque.PECA,
                new BigDecimal("45.00"), 2, 1, true));

        ServicoItem servicoItem = new ServicoItem();
        servicoItem.setId(new ServicoItemId(servico.getId(), item.getId()));
        servicoItem.setServico(servico);
        servicoItem.setItemEstoque(item);
        servicoItem.setQuantidadePadrao(3);
        servicoItemRepository.save(servicoItem);
        orcamentoServicoRepository.save(
                new OrcamentoServico(orcamento, servico, new BigDecimal("255.00")));
        OrdemServico ordemServico =
                persistirOrdemServico(orcamento, StatusOrdemServicoEnum.EM_DIAGNOSTICO);

        assertThatThrownBy(() -> transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.EM_EXECUCAO))
                .isInstanceOf(EstoqueInsuficienteException.class);

        OrdemServico recarregada = ordemServicoRepository.findById(ordemServico.getId()).orElseThrow();
        assertThat(recarregada.getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.EM_DIAGNOSTICO.name());
        assertThat(itemEstoqueRepository.findById(item.getId()).orElseThrow().getQuantidadeAtual())
                .isEqualTo(2);
        assertThat(movimentacaoEstoqueRepository.count()).isZero();

        var historico = historicoStatusOsRepository
                .findByOrdemServico_IdOrderByDataHoraInicioAsc(ordemServico.getId());
        assertThat(historico).hasSize(1);
        assertThat(historico.getFirst().getStatus().getNome())
                .isEqualTo(StatusOrdemServicoEnum.EM_DIAGNOSTICO.name());
        assertThat(historico.getFirst().getDataHoraFim()).isNull();
    }
}
