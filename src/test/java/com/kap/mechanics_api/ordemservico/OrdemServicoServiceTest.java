package com.kap.mechanics_api.ordemservico;

import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.HistoricoStatusOs;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.domain.Usuario;
//import com.kap.mechanics_api.dto.ordemservico.AtualizacaoOrdemServicoRequestDTO;
//import com.kap.mechanics_api.dto.ordemservico.CriacaoOrdemServicoRequestDTO;
//import com.kap.mechanics_api.dto.ordemservico.OrdemServicoResponseDTO;
//import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
//import com.kap.mechanics_api.mapper.OrdemServicoMapper;
//import com.kap.mechanics_api.repository.OrcamentoRepository;
//import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.exception.OrcamentoNaoAprovadoException;
import com.kap.mechanics_api.exception.OrdemServicoJaExisteException;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.HistoricoStatusOsRepository;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import com.kap.mechanics_api.repository.UsuarioRepository;
import com.kap.mechanics_api.service.OrdemServicoService;
import com.kap.mechanics_api.service.TransicaoStatusOrdemServico;
import com.kap.mechanics_api.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock
    private com.kap.mechanics_api.repository.OrcamentoRepository orcamentoRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private StatusOrdemServicoRepository statusOrdemServicoRepository;

    @Mock
    private TransicaoStatusOrdemServico transicaoStatusOrdemServico;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HistoricoStatusOsRepository historicoStatusOsRepository;

    @InjectMocks
    private OrdemServicoService ordemServicoService;

    private Orcamento orcamento;
    private Usuario usuario;
    private StatusOrdemServico statusRecebida;

    @BeforeEach
    void setUp() {
        orcamento = new Orcamento();
        orcamento.setId(1);
        orcamento.setStatusOrcamento(StatusOrcamento.APROVADO);

        usuario = new Usuario();
        usuario.setId(1);

        statusRecebida = new StatusOrdemServico();
        statusRecebida.setId(1);
        statusRecebida.setNome(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name());
    }

    @Test
    void deveGerarOrdemServicoQuandoOrcamentoAprovadoESemOsExistente() {
        Integer orcamentoId = Integer.valueOf(1);

        when(orcamentoRepository.findById(orcamentoId)).thenReturn(Optional.of(orcamento));
        when(ordemServicoRepository.existsByOrcamentoId(orcamentoId)).thenReturn(false);
        when(statusOrdemServicoRepository.findByNome(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name()))
                .thenReturn(Optional.of(statusRecebida));
        when(ordemServicoRepository.save(any(OrdemServico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServico resultado = ordemServicoService.gerarOrdemServico(orcamentoId, usuario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getOrcamento()).isEqualTo(orcamento);
        assertThat(resultado.getUsuarioAtendente()).isEqualTo(usuario);
        assertThat(resultado.getStatusOrdemServico()).isEqualTo(statusRecebida);
        assertThat(resultado.getDataAbertura()).isNotNull();

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);
        verify(ordemServicoRepository).save(captor.capture());
        assertThat(captor.getValue().getStatusOrdemServico().getNome())
                .isEqualTo(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name());

        ArgumentCaptor<HistoricoStatusOs> historicoCaptor = ArgumentCaptor.forClass(HistoricoStatusOs.class);
        verify(historicoStatusOsRepository).save(historicoCaptor.capture());
        assertThat(historicoCaptor.getValue().getOrdemServico()).isEqualTo(resultado);
        assertThat(historicoCaptor.getValue().getStatus()).isEqualTo(statusRecebida);
        assertThat(historicoCaptor.getValue().getDataHoraInicio()).isEqualTo(resultado.getDataAbertura());
        assertThat(historicoCaptor.getValue().getDataHoraFim()).isNull();
    }

    @Test
    void deveAssociarUsuarioLogadoAoGerarOrdemServico() {
        Integer orcamentoId = 1;
        usuario.setLogin("atendente");

        when(usuarioRepository.findByLogin("atendente")).thenReturn(Optional.of(usuario));
        when(orcamentoRepository.findById(orcamentoId)).thenReturn(Optional.of(orcamento));
        when(ordemServicoRepository.existsByOrcamentoId(orcamentoId)).thenReturn(false);
        when(statusOrdemServicoRepository.findByNome(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name()))
                .thenReturn(Optional.of(statusRecebida));
        when(ordemServicoRepository.save(any(OrdemServico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServico resultado = ordemServicoService.gerarOrdemServico(orcamentoId, "atendente");

        assertThat(resultado.getUsuarioAtendente()).isEqualTo(usuario);
        verify(usuarioRepository).findByLogin("atendente");
    }

    @Test
    void deveCriarOrdemServicoPendenteAoGerarOrcamento() {
        Integer orcamentoId = 1;
        usuario.setLogin("atendente");
        orcamento.setStatusOrcamento(StatusOrcamento.PENDENTE);

        when(usuarioRepository.findByLogin("atendente")).thenReturn(Optional.of(usuario));
        when(orcamentoRepository.findById(orcamentoId)).thenReturn(Optional.of(orcamento));
        when(ordemServicoRepository.existsByOrcamentoId(orcamentoId)).thenReturn(false);
        when(statusOrdemServicoRepository.findByNome(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name()))
                .thenReturn(Optional.of(statusRecebida));
        when(ordemServicoRepository.save(any(OrdemServico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServico resultado = ordemServicoService.criarParaOrcamentoPendente(orcamentoId, "atendente");

        assertThat(resultado.getStatusOrdemServico()).isEqualTo(statusRecebida);
        assertThat(resultado.getUsuarioAtendente()).isEqualTo(usuario);
    }

    @Test
    void deveFinalizarOrdemServicoQuandoOrcamentoForRejeitado() {
        Integer orcamentoId = 1;
        OrdemServico ordemServico = new OrdemServico();
        StatusOrdemServico finalizada = new StatusOrdemServico();
        finalizada.setNome(StatusOrdemServicoEnum.FINALIZADA.name());
        ordemServico.setStatusOrdemServico(finalizada);

        when(ordemServicoRepository.findByOrcamento_Id(orcamentoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.save(ordemServico)).thenReturn(ordemServico);

        OrdemServico resultado = ordemServicoService.finalizarPorOrcamento(orcamentoId);

        assertThat(resultado.getStatusOrdemServico()).isEqualTo(finalizada);
        verify(transicaoStatusOrdemServico).finalizarPorOrcamento(ordemServico);
        verify(ordemServicoRepository).save(ordemServico);
    }

    @Test
    void deveLancarExcecaoQuandoOrcamentoNaoEstiverAprovado() {
        Integer orcamentoId = Integer.valueOf(1);
        orcamento.setStatusOrcamento(StatusOrcamento.PENDENTE);

        when(orcamentoRepository.findById(orcamentoId)).thenReturn(Optional.of(orcamento));

        assertThatThrownBy(() -> ordemServicoService.gerarOrdemServico(orcamentoId, usuario))
                .isInstanceOf(OrcamentoNaoAprovadoException.class)
                .hasMessage("Orçamento precisa estar aprovado para gerar OS");

        verify(ordemServicoRepository, never()).save(any());
        verifyNoInteractions(statusOrdemServicoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoOrcamentoJaPossuirOrdemServico() {
        Integer orcamentoId = Integer.valueOf(1);

        when(orcamentoRepository.findById(orcamentoId)).thenReturn(Optional.of(orcamento));
        when(ordemServicoRepository.existsByOrcamentoId(orcamentoId)).thenReturn(true);

        assertThatThrownBy(() -> ordemServicoService.gerarOrdemServico(orcamentoId, usuario))
                .isInstanceOf(OrdemServicoJaExisteException.class)
                .hasMessage("Este orçamento já possui uma OS gerada");

        verify(ordemServicoRepository, never()).save(any());
        verifyNoInteractions(statusOrdemServicoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoStatusRecebidaNaoEstiverCadastrado() {
        Integer orcamentoId = Integer.valueOf(1);

        when(orcamentoRepository.findById(orcamentoId)).thenReturn(Optional.of(orcamento));
        when(ordemServicoRepository.existsByOrcamentoId(orcamentoId)).thenReturn(false);
        when(statusOrdemServicoRepository.findByNome(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordemServicoService.gerarOrdemServico(orcamentoId, usuario))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Status AGUARDANDO_APROVACAO não cadastrado");

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    void deveTransicionarStatusDaOrdemServico() {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setId(Integer.valueOf(9));

        when(ordemServicoRepository.findById(9)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.save(ordemServico)).thenReturn(ordemServico);

        OrdemServico resultado = ordemServicoService
                .transicionarStatus(9, StatusOrdemServicoEnum.EM_DIAGNOSTICO);

        assertEquals(ordemServico, resultado);
        verify(transicaoStatusOrdemServico)
                .transicionar(ordemServico, StatusOrdemServicoEnum.EM_DIAGNOSTICO);
        verify(ordemServicoRepository).save(ordemServico);
    }

}
