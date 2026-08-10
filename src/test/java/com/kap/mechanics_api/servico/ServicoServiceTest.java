package com.kap.mechanics_api.servico;

import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.dto.servico.AtualizacaoServicoRequestDTO;
import com.kap.mechanics_api.dto.servico.ServicoResponseDTO;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.ServicoNaoEncontradoException;
import com.kap.mechanics_api.mapper.ServicoMapper;
import com.kap.mechanics_api.repository.ServicoRepository;
import com.kap.mechanics_api.service.ServicoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {
    @Mock private ServicoRepository repository;
    @Mock private ServicoMapper mapper;
    @InjectMocks private ServicoService service;

    @Test
    void deveRetornarErroQuandoServicoNaoExiste() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoEncontradoException.class, () -> service.buscarPorId(99));
    }

    @Test
    void deveAtualizarSomenteValorDaMaoDeObra() {
        Servico entidade = new Servico("Alinhamento", "Descrição", new BigDecimal("100.00"), 60, true);
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(null, null, new BigDecimal("120.00"), null, null);
        ServicoResponseDTO response = new ServicoResponseDTO("Alinhamento", "Descrição", new BigDecimal("120.00"), 60, true);
        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toAtualizacaoServicoResponseDto(entidade)).thenReturn(response);

        assertEquals(response, service.atualizar(request, 1));
        assertEquals(new BigDecimal("120.00"), entidade.getValorMaoDeObra());
    }

    @Test
    void deveRejeitarAtualizacaoSemCampos() {
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(null, null, null, null, null);

        assertThrows(NenhumCampoInformadoException.class, () -> service.atualizar(request, 1));
    }

    @Test
    void deveExcluirServico() {
        Servico entidade = new Servico("Alinhamento", "Descrição", BigDecimal.TEN, 60, true);
        when(repository.findById(1)).thenReturn(Optional.of(entidade));

        service.deletar(1);

        verify(repository).delete(entidade);
    }
}
