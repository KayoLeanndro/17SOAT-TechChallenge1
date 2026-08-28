package com.kap.mechanics_api.ordemservico;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.dto.ordemservico.AtualizacaoStatusOrdemServicoRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.enums.TipoUsuario;
import com.kap.mechanics_api.repository.ClienteRepository;
import com.kap.mechanics_api.repository.MovimentacaoEstoqueRepository;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import com.kap.mechanics_api.repository.UsuarioRepository;
import com.kap.mechanics_api.repository.VeiculoRepository;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrdemServicoIntegrationTest {

    private static final String ENDPOINT = "/api/ordem-servico";
    private static final String LOGIN_ATENDENTE = "atendente-teste";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private OrcamentoServicoRepository orcamentoServicoRepository;

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

    @BeforeEach
    void limparDadosBaseAntesDoTeste() {
        movimentacaoEstoqueRepository.deleteAll();
        ordemServicoRepository.deleteAll();
        orcamentoServicoRepository.deleteAll();
        orcamentoRepository.deleteAll();
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

    private StatusOrdemServico buscarStatus(StatusOrdemServicoEnum valor) {
        return statusOrdemServicoRepository.findByNome(valor.name()).orElseThrow();
    }

    private Usuario persistirUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(LOGIN_ATENDENTE);
        usuario.setLogin(LOGIN_ATENDENTE);
        usuario.setSenhaHash("hash");
        usuario.setTipo(TipoUsuario.ATENDENTE);
        usuario.setDataCriacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    private Orcamento persistirOrcamento(StatusOrcamento statusOrcamento) {
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
        orcamento.setValorTotal(new java.math.BigDecimal("100.00"));
        orcamento.setStatusOrcamento(statusOrcamento);
        orcamento.setDataCriacao(LocalDateTime.now());
        return orcamentoRepository.save(orcamento);
    }

    private OrdemServico persistirOrdemServico(Orcamento orcamento, Usuario usuario, StatusOrdemServicoEnum statusAtual) {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setOrcamento(orcamento);
        ordemServico.setUsuarioAtendente(usuario);
        ordemServico.setStatusOrdemServico(buscarStatus(statusAtual));
        ordemServico.setDataAbertura(LocalDateTime.now());
        return ordemServicoRepository.save(ordemServico);
    }

    @Test
    @DisplayName("deve gerar a OS para um orçamento aprovado e retornar 201 com o cabeçalho Location")
    void deveGerarOrdemServicoParaOrcamentoAprovado() throws Exception {
        seedStatusOrdemServico();
        Usuario usuario = persistirUsuario();
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.APROVADO);

        mockMvc.perform(post(ENDPOINT + "/" + orcamento.getId()).with(atendente()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/ordem-servico/")));

        assertThat(ordemServicoRepository.findAll()).hasSize(1);
        OrdemServico gerada = ordemServicoRepository.findByOrcamento_Id(orcamento.getId()).orElseThrow();
        assertThat(gerada.getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name());
        assertThat(gerada.getUsuarioAtendente().getId()).isEqualTo(usuario.getId());
    }

    @Test
    @DisplayName("deve retornar 404 ao gerar a OS para um orçamento que não está aprovado")
    void deveRetornar404AoGerarOrdemServicoParaOrcamentoNaoAprovado() throws Exception {
        seedStatusOrdemServico();
        persistirUsuario();
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.PENDENTE);

        mockMvc.perform(post(ENDPOINT + "/" + orcamento.getId()).with(atendente()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Orçamento precisa estar aprovado para gerar OS"));

        assertThat(ordemServicoRepository.count()).isZero();
    }

    @Test
    @DisplayName("deve retornar 404 ao gerar a OS para um orçamento inexistente")
    void deveRetornar404AoGerarOrdemServicoParaOrcamentoInexistente() throws Exception {
        seedStatusOrdemServico();
        persistirUsuario();

        mockMvc.perform(post(ENDPOINT + "/999999").with(atendente()))
                .andExpect(status().isNotFound());

        assertThat(ordemServicoRepository.count()).isZero();
    }

    @Test
    @DisplayName("deve retornar 404 ao gerar a OS quando o orçamento já possui uma OS")
    void deveRetornar404AoGerarOrdemServicoQuandoJaExiste() throws Exception {
        seedStatusOrdemServico();
        Usuario usuario = persistirUsuario();
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.APROVADO);
        persistirOrdemServico(orcamento, usuario, StatusOrdemServicoEnum.AGUARDANDO_APROVACAO);

        mockMvc.perform(post(ENDPOINT + "/" + orcamento.getId()).with(atendente()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Este orçamento já possui uma OS gerada"));

        assertThat(ordemServicoRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("deve transicionar a OS de AGUARDANDO_APROVACAO para EM_DIAGNOSTICO e retornar 204")
    void deveTransicionarStatusDaOrdemServico() throws Exception {
        seedStatusOrdemServico();
        Usuario usuario = persistirUsuario();
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.APROVADO);
        OrdemServico ordemServico =
                persistirOrdemServico(orcamento, usuario, StatusOrdemServicoEnum.AGUARDANDO_APROVACAO);

        AtualizacaoStatusOrdemServicoRequestDTO request =
                new AtualizacaoStatusOrdemServicoRequestDTO(StatusOrdemServicoEnum.EM_DIAGNOSTICO);

        mockMvc.perform(patch(ENDPOINT + "/" + ordemServico.getId() + "/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertThat(ordemServicoRepository.findById(ordemServico.getId()).orElseThrow()
                .getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.EM_DIAGNOSTICO.name());
    }

    @Test
    @DisplayName("deve transicionar de EM_DIAGNOSTICO para EM_EXECUCAO acionando a baixa de estoque mesmo sem itens orçados")
    void deveTransicionarParaEmExecucaoSemItensOrcados() throws Exception {
        seedStatusOrdemServico();
        Usuario usuario = persistirUsuario();
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.APROVADO);
        OrdemServico ordemServico =
                persistirOrdemServico(orcamento, usuario, StatusOrdemServicoEnum.EM_DIAGNOSTICO);

        AtualizacaoStatusOrdemServicoRequestDTO request =
                new AtualizacaoStatusOrdemServicoRequestDTO(StatusOrdemServicoEnum.EM_EXECUCAO);

        mockMvc.perform(patch(ENDPOINT + "/" + ordemServico.getId() + "/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertThat(ordemServicoRepository.findById(ordemServico.getId()).orElseThrow()
                .getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.EM_EXECUCAO.name());
        assertThat(movimentacaoEstoqueRepository.count()).isZero();
    }

    @Test
    @DisplayName("deve retornar 400 ao solicitar uma transição de status inválida")
    void deveRetornar400ParaTransicaoInvalida() throws Exception {
        seedStatusOrdemServico();
        Usuario usuario = persistirUsuario();
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.APROVADO);
        OrdemServico ordemServico =
                persistirOrdemServico(orcamento, usuario, StatusOrdemServicoEnum.AGUARDANDO_APROVACAO);

        AtualizacaoStatusOrdemServicoRequestDTO request =
                new AtualizacaoStatusOrdemServicoRequestDTO(StatusOrdemServicoEnum.FINALIZADA);

        mockMvc.perform(patch(ENDPOINT + "/" + ordemServico.getId() + "/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Transicao de status invalida"));

        assertThat(ordemServicoRepository.findById(ordemServico.getId()).orElseThrow()
                .getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name());
    }

    @Test
    @DisplayName("deve retornar 400 ao transicionar o status sem informar o novo status")
    void deveRetornar400QuandoNovoStatusNaoInformado() throws Exception {
        seedStatusOrdemServico();
        Usuario usuario = persistirUsuario();
        Orcamento orcamento = persistirOrcamento(StatusOrcamento.APROVADO);
        OrdemServico ordemServico =
                persistirOrdemServico(orcamento, usuario, StatusOrdemServicoEnum.AGUARDANDO_APROVACAO);

        mockMvc.perform(patch(ENDPOINT + "/" + ordemServico.getId() + "/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deve retornar 404 ao transicionar o status de uma OS inexistente")
    void deveRetornar404AoTransicionarStatusDeOrdemServicoInexistente() throws Exception {
        seedStatusOrdemServico();
        AtualizacaoStatusOrdemServicoRequestDTO request =
                new AtualizacaoStatusOrdemServicoRequestDTO(StatusOrdemServicoEnum.EM_DIAGNOSTICO);

        mockMvc.perform(patch(ENDPOINT + "/999999/status")
                        .with(atendente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ordem de serviço não encontrada"));
    }

    @Test
    @DisplayName("deve retornar 401 ao acessar a ordem de serviço sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(post(ENDPOINT + "/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("deve retornar 403 ao acessar a ordem de serviço com um papel sem permissão")
    void deveRetornar403ParaPapelSemPermissao() throws Exception {
        AtualizacaoStatusOrdemServicoRequestDTO request =
                new AtualizacaoStatusOrdemServicoRequestDTO(StatusOrdemServicoEnum.EM_DIAGNOSTICO);

        mockMvc.perform(patch(ENDPOINT + "/1/status")
                        .with(definirRole("ESTOQUISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
