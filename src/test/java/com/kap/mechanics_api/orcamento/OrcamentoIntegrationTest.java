package com.kap.mechanics_api.orcamento;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.domain.ItemEstoque;
import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.domain.ServicoItem;
import com.kap.mechanics_api.domain.ServicoItemId;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.dto.orcamento.AtualizacaoStatusOrcamentoRequestDTO;
import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.enums.TipoItemEstoque;
import com.kap.mechanics_api.enums.TipoUsuario;
import com.kap.mechanics_api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrcamentoIntegrationTest {

    private static final String ENDPOINT = "/api/orcamento";
    private static final String LOGIN_ATENDENTE = "atendente-teste";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private OrcamentoServicoRepository orcamentoServicoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private StatusOrdemServicoRepository statusOrdemServicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ServicoItemRepository servicoItemRepository;

    @Autowired
    private ItemEstoqueRepository itemEstoqueRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistoricoStatusOsRepository historicoStatusOsRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;


    @Autowired
    private OrcamentoItemRepository orcamentoItemRepository;

    @BeforeEach
    void limparDadosBaseAntesDoTeste() {
        servicoItemRepository.deleteAll();
        orcamentoItemRepository.deleteAll();
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
    }

    private RequestPostProcessor definirRole(String role) {
        return jwt()
                .jwt(jwt -> jwt.subject(LOGIN_ATENDENTE))
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    private RequestPostProcessor atendente() {
        return definirRole("ATENDENTE");
    }

    private void seedStatusOrdemServico() {
        for (StatusOrdemServicoEnum valor : StatusOrdemServicoEnum.values()) {
            StatusOrdemServico status = new StatusOrdemServico();
            status.setNome(valor.name());
            statusOrdemServicoRepository.save(status);
        }
    }

    private Usuario persistirUsuario(String login) {
        Usuario usuario = new Usuario();
        usuario.setNome(login);
        usuario.setLogin(login);
        usuario.setSenhaHash("hash");
        usuario.setTipo(TipoUsuario.ATENDENTE);
        usuario.setDataCriacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    private Cliente persistirCliente() {
        Cliente cliente = new Cliente("João Silva", "12345678900", "51999999999", "joao@email.com", LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    private Veiculo persistirVeiculo() {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("Honda");
        veiculo.setModelo("Civic");
        veiculo.setAno(2020);
        veiculo.setDataCriacao(LocalDateTime.now());
        return veiculoRepository.save(veiculo);
    }

    private Servico persistirServico(String nome, String valorMaoDeObra) {
        Servico servico = new Servico(nome, "Descricao " + nome, new BigDecimal(valorMaoDeObra), 60, true);
        return servicoRepository.save(servico);
    }

    private void vincularItemAoServico(Servico servico, String valorUnitario, int quantidadePadrao) {
        ItemEstoque item = itemEstoqueRepository.save(new ItemEstoque(
                "Peca " + servico.getNome(), "Peca", TipoItemEstoque.PECA,
                new BigDecimal(valorUnitario), 100, 1, true));

        ServicoItem servicoItem = new ServicoItem();
        servicoItem.setId(new ServicoItemId(servico.getId(), item.getId()));
        servicoItem.setServico(servico);
        servicoItem.setItemEstoque(item);
        servicoItem.setQuantidadePadrao(quantidadePadrao);
        servicoItemRepository.save(servicoItem);
    }

    private Orcamento persistirOrcamento(StatusOrcamento status) {
        Orcamento orcamento = new Orcamento();
        orcamento.setCliente(persistirCliente());
        orcamento.setVeiculo(persistirVeiculo());
        orcamento.setValorTotal(new BigDecimal("100.00"));
        orcamento.setStatusOrcamento(status);
        orcamento.setDataCriacao(LocalDateTime.now());
        return orcamentoRepository.save(orcamento);
    }

    /**
     * Gera um orçamento PENDENTE pelo fluxo real da API, o que também abre a Ordem de Serviço
     * associada em AGUARDANDO_APROVACAO. Necessário para exercitar a atualização de status, que
     * transiciona a OS ao aprovar/rejeitar o orçamento.
     */
    private Orcamento gerarOrcamentoPendenteViaApi() throws Exception {
        persistirUsuario(LOGIN_ATENDENTE);
        seedStatusOrdemServico();
        Cliente cliente = persistirCliente();
        Veiculo veiculo = persistirVeiculo();
        Servico servico = persistirServico("Troca de oleo", "100.00");

        GeracaoOrcamentoRequestDTO request =
                new GeracaoOrcamentoRequestDTO(cliente.getId(), veiculo.getId(), List.of(servico.getId()));

        mockMvc.perform(post(ENDPOINT + "/gerarOrcamento")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        return orcamentoRepository.findAll().get(0);
    }

    @Test
    @DisplayName("deve gerar orçamento pendente, calcular o valor total e abrir uma OS aguardando aprovação")
    void deveGerarOrcamentoPendenteEAbrirOrdemServico() throws Exception {
        persistirUsuario(LOGIN_ATENDENTE);
        seedStatusOrdemServico();
        Cliente cliente = persistirCliente();
        Veiculo veiculo = persistirVeiculo();
        Servico servico = persistirServico("Troca de oleo", "150.00");

        GeracaoOrcamentoRequestDTO request =
                new GeracaoOrcamentoRequestDTO(cliente.getId(), veiculo.getId(), List.of(servico.getId()));

        mockMvc.perform(post(ENDPOINT + "/gerarOrcamento")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Orçamento gerado com sucesso."));

        List<Orcamento> orcamentos = orcamentoRepository.findAll();
        assertThat(orcamentos).hasSize(1);
        Orcamento orcamento = orcamentos.get(0);
        assertThat(orcamento.getStatusOrcamento()).isEqualTo(StatusOrcamento.PENDENTE);
        assertThat(orcamento.getValorTotal()).isEqualByComparingTo("150.00");

        assertThat(orcamentoServicoRepository.findByOrcamento_Id(orcamento.getId())).hasSize(1);

        assertThat(ordemServicoRepository.findAll()).hasSize(1);
        assertThat(ordemServicoRepository.findByOrcamento_Id(orcamento.getId()).orElseThrow()
                .getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name());
    }

    @Test
    @DisplayName("deve somar a mão de obra de múltiplos serviços e seus itens no valor total do orçamento")
    void deveSomarMaoDeObraEItensDeMultiplosServicos() throws Exception {
        persistirUsuario(LOGIN_ATENDENTE);
        seedStatusOrdemServico();
        Cliente cliente = persistirCliente();
        Veiculo veiculo = persistirVeiculo();
        Servico servicoA = persistirServico("Alinhamento", "100.00");
        Servico servicoB = persistirServico("Balanceamento", "80.00");
        vincularItemAoServico(servicoB, "10.00", 2);

        GeracaoOrcamentoRequestDTO request = new GeracaoOrcamentoRequestDTO(
                cliente.getId(), veiculo.getId(), List.of(servicoA.getId(), servicoB.getId()));

        mockMvc.perform(post(ENDPOINT + "/gerarOrcamento")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Orcamento orcamento = orcamentoRepository.findAll().get(0);
        assertThat(orcamento.getValorTotal()).isEqualByComparingTo("200.00");
        assertThat(orcamentoServicoRepository.findByOrcamento_Id(orcamento.getId())).hasSize(2);
    }

    @Test
    @DisplayName("deve retornar 400 ao gerar orçamento sem informar o cliente")
    void deveRetornar400AoGerarOrcamentoSemCliente() throws Exception {
        GeracaoOrcamentoRequestDTO request =
                new GeracaoOrcamentoRequestDTO(null, 1, List.of(1));

        mockMvc.perform(post(ENDPOINT + "/gerarOrcamento")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(orcamentoRepository.count()).isZero();
    }

    @Test
    @DisplayName("deve retornar 404 ao gerar orçamento para um cliente inexistente")
    void deveRetornar404AoGerarOrcamentoParaClienteInexistente() throws Exception {
        persistirUsuario(LOGIN_ATENDENTE);
        GeracaoOrcamentoRequestDTO request =
                new GeracaoOrcamentoRequestDTO(999999, 999999, List.of(999999));

        mockMvc.perform(post(ENDPOINT + "/gerarOrcamento")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertThat(orcamentoRepository.count()).isZero();
    }

    @Test
    @DisplayName("deve atualizar o status do orçamento para APROVADO e registrar a data de resposta")
    void deveAtualizarStatusDoOrcamentoParaAprovado() throws Exception {
        Orcamento orcamento = gerarOrcamentoPendenteViaApi();
        AtualizacaoStatusOrcamentoRequestDTO request = new AtualizacaoStatusOrcamentoRequestDTO("APROVADO");

        mockMvc.perform(patch(ENDPOINT + "/" + orcamento.getId() + "/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("status do orçamento atualizado com sucesso!"));

        Orcamento atualizado = orcamentoRepository.findById(orcamento.getId()).orElseThrow();
        assertThat(atualizado.getStatusOrcamento()).isEqualTo(StatusOrcamento.APROVADO);
        assertThat(atualizado.getDataResposta()).isNotNull();
    }

    @Test
    @DisplayName("deve aceitar o status informado em caixa baixa ao atualizar o orçamento")
    void deveAceitarStatusEmCaixaBaixa() throws Exception {
        Orcamento orcamento = gerarOrcamentoPendenteViaApi();
        AtualizacaoStatusOrcamentoRequestDTO request = new AtualizacaoStatusOrcamentoRequestDTO("aprovado");

        mockMvc.perform(patch(ENDPOINT + "/" + orcamento.getId() + "/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(orcamentoRepository.findById(orcamento.getId()).orElseThrow().getStatusOrcamento())
                .isEqualTo(StatusOrcamento.APROVADO);
    }

    @Test
    @DisplayName("deve retornar 400 ao atualizar o orçamento com um status inexistente")
    void deveRetornar400AoAtualizarComStatusInvalido() throws Exception {
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.PENDENTE);
        AtualizacaoStatusOrcamentoRequestDTO request = new AtualizacaoStatusOrcamentoRequestDTO("FINALIZADO");

        mockMvc.perform(patch(ENDPOINT + "/" + orcamento.getId() + "/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Status de orçamento inválido"));

        assertThat(orcamentoRepository.findById(orcamento.getId()).orElseThrow().getStatusOrcamento())
                .isEqualTo(StatusOrcamento.PENDENTE);
    }

    @Test
    @DisplayName("deve retornar 400 ao atualizar o status do orçamento com o corpo em branco")
    void deveRetornar400AoAtualizarStatusComCorpoEmBranco() throws Exception {
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.PENDENTE);
        AtualizacaoStatusOrcamentoRequestDTO request = new AtualizacaoStatusOrcamentoRequestDTO("");

        mockMvc.perform(patch(ENDPOINT + "/" + orcamento.getId() + "/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deve retornar 404 ao atualizar o status de um orçamento inexistente")
    void deveRetornar404AoAtualizarStatusDeOrcamentoInexistente() throws Exception {
        AtualizacaoStatusOrcamentoRequestDTO request = new AtualizacaoStatusOrcamentoRequestDTO("APROVADO");

        mockMvc.perform(patch(ENDPOINT + "/999999/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Orçamento não encontrado"));
    }

    @Test
    @DisplayName("deve retornar 401 ao acessar o orçamento sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(post(ENDPOINT + "/gerarOrcamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("deve retornar 403 ao acessar o orçamento com um papel sem permissão")
    void deveRetornar403ParaPapelSemPermissao() throws Exception {
        AtualizacaoStatusOrcamentoRequestDTO request = new AtualizacaoStatusOrcamentoRequestDTO("APROVADO");

        mockMvc.perform(patch(ENDPOINT + "/1/status")
                        .with(definirRole("ESTOQUISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
