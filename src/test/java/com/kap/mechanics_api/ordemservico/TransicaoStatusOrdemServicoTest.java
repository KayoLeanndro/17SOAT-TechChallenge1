package com.kap.mechanics_api.ordemservico;

import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.exception.TransicaoStatusInvalidaException;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import com.kap.mechanics_api.service.TransicaoStatusOrdemServico;
import com.kap.mechanics_api.service.MovimentacaoEstoqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransicaoStatusOrdemServicoTest {

    @Mock
    private StatusOrdemServicoRepository statusOrdemServicoRepository;

    @Mock
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @InjectMocks
    private TransicaoStatusOrdemServico transicaoStatusOrdemServico;

    private OrdemServico ordemServico;

    private StatusOrdemServico statusOrdemServico(String nome) {
        StatusOrdemServico status = new StatusOrdemServico();
        status.setId(1);
        status.setNome(nome);
        return status;
    }

    @BeforeEach
    void setUp() {
        ordemServico = new OrdemServico();
    }

    @Test
    void devePermitirTransicaoDeRecebidaParaEmDiagnostico() {
        ordemServico.setStatusOrdemServico(statusOrdemServico(StatusOrdemServicoEnum.RECEBIDA.name()));

        StatusOrdemServico statusDestino = statusOrdemServico(StatusOrdemServicoEnum.EM_DIAGNOSTICO.name());
        when(statusOrdemServicoRepository.findByNome(StatusOrdemServicoEnum.EM_DIAGNOSTICO.name()))
                .thenReturn(Optional.of(statusDestino));

        transicaoStatusOrdemServico.transicionar(ordemServico, StatusOrdemServicoEnum.EM_DIAGNOSTICO);

        assertThat(ordemServico.getStatusOrdemServico()).isEqualTo(statusDestino);
    }

    @ParameterizedTest
    @CsvSource({
            "AGUARDANDO_APROVACAO, EM_DIAGNOSTICO",
            "EM_DIAGNOSTICO, EM_EXECUCAO",
            "EM_EXECUCAO, FINALIZADA",
            "FINALIZADA, ENTREGUE"
    })
    void devePermitirTodasAsTransicoesValidasDoFluxo(String statusAtual, String statusDestino) {
        ordemServico.setStatusOrdemServico(statusOrdemServico(statusAtual));

        StatusOrdemServicoEnum destino = StatusOrdemServicoEnum.valueOf(statusDestino);
        when(statusOrdemServicoRepository.findByNome(statusDestino))
                .thenReturn(Optional.of(statusOrdemServico(statusDestino)));

        transicaoStatusOrdemServico.transicionar(ordemServico, destino);

        assertThat(ordemServico.getStatusOrdemServico().getNome()).isEqualTo(statusDestino);

        if (destino == StatusOrdemServicoEnum.EM_EXECUCAO) {
            verify(movimentacaoEstoqueService).baixarItensDaOrdemServico(ordemServico);
        }
    }

    @Test
    void deveLancarExcecaoAoTentarPularEtapaDoFluxo() {
        ordemServico.setStatusOrdemServico(statusOrdemServico(StatusOrdemServicoEnum.RECEBIDA.name()));

        assertThatThrownBy(() -> transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.EM_EXECUCAO))
                .isInstanceOf(TransicaoStatusInvalidaException.class)
                .hasMessageContaining("RECEBIDA")
                .hasMessageContaining("EM_EXECUCAO");

        verifyNoInteractions(statusOrdemServicoRepository);
    }

    @Test
    void deveLancarExcecaoAoTentarTransicaoParaTrasNoFluxo() {
        ordemServico.setStatusOrdemServico(statusOrdemServico(StatusOrdemServicoEnum.EM_EXECUCAO.name()));

        assertThatThrownBy(() -> transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.RECEBIDA))
                .isInstanceOf(TransicaoStatusInvalidaException.class);

        verifyNoInteractions(statusOrdemServicoRepository);
    }

    @Test
    void deveLancarExcecaoAoTentarTransicionarOrdemServicoJaEntregue() {
        ordemServico.setStatusOrdemServico(statusOrdemServico(StatusOrdemServicoEnum.ENTREGUE.name()));

        assertThatThrownBy(() -> transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.FINALIZADA))
                .isInstanceOf(TransicaoStatusInvalidaException.class);

        verifyNoInteractions(statusOrdemServicoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoStatusDestinoValidoNaoEstiverCadastradoNoBanco() {
        ordemServico.setStatusOrdemServico(statusOrdemServico(StatusOrdemServicoEnum.RECEBIDA.name()));

        when(statusOrdemServicoRepository.findByNome(StatusOrdemServicoEnum.EM_DIAGNOSTICO.name()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transicaoStatusOrdemServico.transicionar(
                ordemServico, StatusOrdemServicoEnum.EM_DIAGNOSTICO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");
    }

}
