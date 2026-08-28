package com.kap.mechanics_api.orcamento;

import com.kap.mechanics_api.domain.*;
import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import com.kap.mechanics_api.service.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrcamentoServicoTest {

    @Test
    void construtorPadraoDeveIniciarComTodosOsCamposNulos() {
        OrcamentoServico orcamentoServico = new OrcamentoServico();

        assertNull(orcamentoServico.getId());
        assertNull(orcamentoServico.getOrcamento());
        assertNull(orcamentoServico.getServico());
        assertNull(orcamentoServico.getValorCobrado());
    }

    @Test
    void construtorComArgumentosDevePreencherOrcamentoServicoEValorCobrado() {
        Orcamento orcamento = new Orcamento();
        Servico servico = new Servico();
        BigDecimal valorCobrado = new BigDecimal("150.00");

        OrcamentoServico orcamentoServico = new OrcamentoServico(orcamento, servico, valorCobrado);

        assertEquals(orcamento, orcamentoServico.getOrcamento());
        assertEquals(servico, orcamentoServico.getServico());
        assertEquals(valorCobrado, orcamentoServico.getValorCobrado());
    }

    @Test
    void construtorComArgumentosNaoDeveDefinirIdAutomaticamente() {
        // O id é um @EmbeddedId com @MapsId, então deve ser derivado
        // pelo JPA a partir de orcamento/servico persistidos, não pelo construtor.
        OrcamentoServico orcamentoServico = new OrcamentoServico(
                new Orcamento(), new Servico(), BigDecimal.TEN);

        assertNull(orcamentoServico.getId());
    }

    @Test
    void setIdDeveAtualizarIdCorretamente() {
        OrcamentoServico orcamentoServico = new OrcamentoServico();
        OrcamentoServicoId id = new OrcamentoServicoId(1, 10);

        orcamentoServico.setId(id);

        assertEquals(id, orcamentoServico.getId());
    }

    @Test
    void setOrcamentoDeveAtualizarOrcamentoCorretamente() {
        OrcamentoServico orcamentoServico = new OrcamentoServico();
        Orcamento orcamento = new Orcamento();
        orcamento.setId(5);

        orcamentoServico.setOrcamento(orcamento);

        assertEquals(orcamento, orcamentoServico.getOrcamento());
        assertEquals(5, orcamentoServico.getOrcamento().getId());
    }

    @Test
    void setServicoDeveAtualizarServicoCorretamente() {
        OrcamentoServico orcamentoServico = new OrcamentoServico();
        Servico servico = new Servico();
        servico.setId(7);

        orcamentoServico.setServico(servico);

        assertEquals(servico, orcamentoServico.getServico());
        assertEquals(7, orcamentoServico.getServico().getId());
    }

    @Test
    void setValorCobradoDeveAtualizarValorCorretamente() {
        OrcamentoServico orcamentoServico = new OrcamentoServico();
        BigDecimal novoValor = new BigDecimal("299.90");

        orcamentoServico.setValorCobrado(novoValor);

        assertEquals(novoValor, orcamentoServico.getValorCobrado());
    }

    @Test
    void setValorCobradoDevePermitirValorZero() {
        OrcamentoServico orcamentoServico = new OrcamentoServico();

        orcamentoServico.setValorCobrado(BigDecimal.ZERO);

        assertEquals(0, BigDecimal.ZERO.compareTo(orcamentoServico.getValorCobrado()));
    }

    @Test
    void setValorCobradoDevePermitirValorNulo() {
        OrcamentoServico orcamentoServico = new OrcamentoServico(
                new Orcamento(), new Servico(), BigDecimal.TEN);

        orcamentoServico.setValorCobrado(null);

        assertNull(orcamentoServico.getValorCobrado());
    }

}
