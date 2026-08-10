package com.kap.mechanics_api.peca;

import com.kap.mechanics_api.domain.Peca;
import com.kap.mechanics_api.dto.peca.AtualizacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.CriacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.PecaResponseDTO;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.PecaNaoEncontradaException;
import com.kap.mechanics_api.mapper.PecaMapper;
import com.kap.mechanics_api.repository.PecaRepository;
import com.kap.mechanics_api.service.PecaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PecaServiceTest {
    @Mock private PecaRepository repository;
    @Mock private PecaMapper mapper;
    @InjectMocks private PecaService service;

    @Test
    void deveCadastrarPeca() {
        CriacaoPecaRequestDTO request = new CriacaoPecaRequestDTO("Pastilha", "Pastilha de freio", new BigDecimal("90.00"), 5, 2, true);
        Peca entidade = peca();
        PecaResponseDTO response = response(5);
        when(mapper.toEntity(request)).thenReturn(entidade);
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(response);

        assertEquals(response, service.cadastrar(request));
        verify(repository).save(entidade);
    }

    @Test
    void deveListarPecas() {
        Peca entidade = peca();
        when(repository.findAll()).thenReturn(List.of(entidade));
        when(mapper.toResponseDtoList(List.of(entidade))).thenReturn(List.of(response(5)));

        assertEquals(1, service.listar().size());
    }

    @Test
    void deveRetornarErroQuandoPecaNaoExiste() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(PecaNaoEncontradaException.class, () -> service.buscarPorId(99));
    }

    @Test
    void deveAtualizarSomenteQuantidade() {
        Peca entidade = peca();
        AtualizacaoPecaRequestDTO request = new AtualizacaoPecaRequestDTO(null, null, null, 12, null, null);
        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(response(12));

        service.atualizar(1, request);

        assertEquals(12, entidade.getQuantidadeAtual());
        verify(repository).save(entidade);
    }

    @Test
    void deveRejeitarAtualizacaoSemCampos() {
        AtualizacaoPecaRequestDTO request = new AtualizacaoPecaRequestDTO(null, null, null, null, null, null);

        assertThrows(NenhumCampoInformadoException.class, () -> service.atualizar(1, request));
    }

    @Test
    void deveExcluirPeca() {
        Peca entidade = peca();
        when(repository.findById(1)).thenReturn(Optional.of(entidade));

        service.deletar(1);

        verify(repository).delete(entidade);
    }

    private Peca peca() { return new Peca(true, "Pastilha", "Pastilha de freio", new BigDecimal("90.00"), 5, 2); }
    private PecaResponseDTO response(int quantidade) { return new PecaResponseDTO(1, "Pastilha", "Pastilha de freio", new BigDecimal("90.00"), quantidade, 2, true); }
}
