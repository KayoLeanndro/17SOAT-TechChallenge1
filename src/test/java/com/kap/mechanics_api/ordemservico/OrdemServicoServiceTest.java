package com.kap.mechanics_api.ordemservico;

import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.dto.ordemservico.AtualizacaoOrdemServicoRequestDTO;
import com.kap.mechanics_api.dto.ordemservico.CriacaoOrdemServicoRequestDTO;
import com.kap.mechanics_api.dto.ordemservico.OrdemServicoResponseDTO;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.mapper.OrdemServicoMapper;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.service.OrdemServicoService;
import com.kap.mechanics_api.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;
    @Mock
    private OrcamentoRepository orcamentoRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private OrdemServicoMapper ordemServicoMapper;

    @InjectMocks
    private OrdemServicoService ordemServicoService;

    @Test
    void deveCadastrarOrdemServicoComRelacionamentosInformados() {
        CriacaoOrdemServicoRequestDTO dto = new CriacaoOrdemServicoRequestDTO(1, 2, 3);
        OrdemServico ordemServico = new OrdemServico();
        OrdemServicoResponseDTO response = new OrdemServicoResponseDTO(10, 1, 2, 3,
                "RECEBIDA", null, null);

        when(ordemServicoMapper.toEntity(dto)).thenReturn(ordemServico);
        when(orcamentoRepository.findById(1)).thenReturn(Optional.of(new Orcamento()));
        when(usuarioService.buscarPorId(2)).thenReturn(new Usuario());
//        when(statusOrdemServicoRepository.findById(3)).thenReturn(Optional.of(new StatusOrdemServico()));
        when(ordemServicoRepository.save(ordemServico)).thenReturn(ordemServico);
        when(ordemServicoMapper.toResponseDto(ordemServico)).thenReturn(response);

        OrdemServicoResponseDTO resultado = ordemServicoService.cadastrar(dto);

        assertEquals(response, resultado);
        verify(ordemServicoRepository).save(ordemServico);
    }

    @Test
    void deveRejeitarAtualizacaoSemCampos() {
        AtualizacaoOrdemServicoRequestDTO dto = new AtualizacaoOrdemServicoRequestDTO(null, null, null);

        assertThrows(NenhumCampoInformadoException.class,
                () -> ordemServicoService.atualizar(1, dto));
    }

    @Test
    void deveAlterarAtendenteEStatusQuandoInformados() {
        OrdemServico ordemServico = new OrdemServico();
        OrdemServicoResponseDTO response = new OrdemServicoResponseDTO(1, 1, 4, 5,
                "EM_EXECUCAO", null, null);
        AtualizacaoOrdemServicoRequestDTO dto = new AtualizacaoOrdemServicoRequestDTO(4, 5, null);

        when(ordemServicoRepository.findById(1)).thenReturn(Optional.of(ordemServico));
        when(usuarioService.buscarPorId(4)).thenReturn(new Usuario());
//        when(statusOrdemServicoRepository.findById(5)).thenReturn(Optional.of(new StatusOrdemServico()));
        when(ordemServicoRepository.save(ordemServico)).thenReturn(ordemServico);
        when(ordemServicoMapper.toResponseDto(ordemServico)).thenReturn(response);

        assertEquals(response, ordemServicoService.atualizar(1, dto));
        verify(ordemServicoRepository).save(ordemServico);
    }
}
