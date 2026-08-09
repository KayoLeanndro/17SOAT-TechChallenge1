package com.kap.mechanics_api.insumo;

import com.kap.mechanics_api.domain.Insumo;
import com.kap.mechanics_api.dto.insumo.AtualizacaoInsumoRequestDTO;
import com.kap.mechanics_api.dto.insumo.CriacaoInsumoRequestDTO;
import com.kap.mechanics_api.dto.insumo.InsumoResponseDTO;
import com.kap.mechanics_api.exception.InsumoNaoEncontradoException;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.mapper.InsumoMapper;
import com.kap.mechanics_api.repository.InsumoRepository;
import com.kap.mechanics_api.service.InsumoService;
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
class InsumoServiceTest {
    @Mock private InsumoRepository repository;
    @Mock private InsumoMapper mapper;
    @InjectMocks private InsumoService service;

    @Test
    void deveCadastrarInsumo() {
        CriacaoInsumoRequestDTO request = new CriacaoInsumoRequestDTO("Óleo", "Óleo de motor", new BigDecimal("40.00"), 10, 3, true);
        Insumo entidade = insumo();
        when(mapper.toEntity(request)).thenReturn(entidade);
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(response(10));

        assertEquals(response(10), service.cadastrar(request));
    }

    @Test
    void deveListarInsumos() {
        Insumo entidade = insumo();
        when(repository.findAll()).thenReturn(List.of(entidade));
        when(mapper.toResponseDtoList(List.of(entidade))).thenReturn(List.of(response(10)));

        assertEquals(1, service.listar().size());
    }

    @Test
    void deveRetornarErroQuandoInsumoNaoExiste() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(InsumoNaoEncontradoException.class, () -> service.buscarPorId(99));
    }

    @Test
    void deveAtualizarSomenteQuantidade() {
        Insumo entidade = insumo();
        AtualizacaoInsumoRequestDTO request = new AtualizacaoInsumoRequestDTO(null, null, null, 20, null, null);
        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(response(20));

        service.atualizar(1, request);

        assertEquals(20, entidade.getQuantidadeAtual());
        verify(repository).save(entidade);
    }

    @Test
    void deveRejeitarAtualizacaoSemCampos() {
        AtualizacaoInsumoRequestDTO request = new AtualizacaoInsumoRequestDTO(null, null, null, null, null, null);

        assertThrows(NenhumCampoInformadoException.class, () -> service.atualizar(1, request));
    }

    @Test
    void deveExcluirInsumo() {
        Insumo entidade = insumo();
        when(repository.findById(1)).thenReturn(Optional.of(entidade));

        service.deletar(1);

        verify(repository).delete(entidade);
    }

    private Insumo insumo() { return new Insumo("Óleo", "Óleo de motor", new BigDecimal("40.00"), 10, 3, true); }
    private InsumoResponseDTO response(int quantidade) { return new InsumoResponseDTO(1, "Óleo", "Óleo de motor", new BigDecimal("40.00"), quantidade, 3, true); }
}
