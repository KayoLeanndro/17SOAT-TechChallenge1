package com.kap.mechanics_api.servico;

import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.dto.servico.AtualizacaoServicoRequestDTO;
import com.kap.mechanics_api.dto.servico.CriacaoServicoRequestDTO;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        when(mapper.toResponseDto(entidade)).thenReturn(response);

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

    @Test
    void deveCadastrarServicoComSucesso() {
        CriacaoServicoRequestDTO dto = new CriacaoServicoRequestDTO(
                "Troca de óleo", "Descrição", new BigDecimal("80.00"), 30, true);
        Servico entidadeMapeada = new Servico("Troca de óleo", "Descrição", new BigDecimal("80.00"), 30, true);
        Servico entidadeSalva = new Servico("Troca de óleo", "Descrição", new BigDecimal("80.00"), 30, true);
        entidadeSalva.setId(1);
        ServicoResponseDTO response = new ServicoResponseDTO(
                "Troca de óleo", "Descrição", new BigDecimal("80.00"), 30, true);

        when(mapper.toEntity(dto)).thenReturn(entidadeMapeada);
        when(repository.save(entidadeMapeada)).thenReturn(entidadeSalva);
        when(mapper.toResponseDto(entidadeSalva)).thenReturn(response);

        ServicoResponseDTO resultado = service.cadastrar(dto);

        assertEquals(response, resultado);
        verify(mapper).toEntity(dto);
        verify(repository).save(entidadeMapeada);
        verify(mapper).toResponseDto(entidadeSalva);
    }

    @Test
    void deveListarTodosOsServicos() {
        Servico servico1 = new Servico("Alinhamento", "Descrição", BigDecimal.TEN, 60, true);
        Servico servico2 = new Servico("Balanceamento", "Descrição", BigDecimal.ONE, 40, true);
        List<Servico> entidades = List.of(servico1, servico2);
        List<ServicoResponseDTO> respostaEsperada = List.of(
                new ServicoResponseDTO("Alinhamento", "Descrição", BigDecimal.TEN, 60, true),
                new ServicoResponseDTO("Balanceamento", "Descrição", BigDecimal.ONE, 40, true)
        );

        when(repository.findAll()).thenReturn(entidades);
        when(mapper.toListagemDto(entidades)).thenReturn(respostaEsperada);

        List<ServicoResponseDTO> resultado = service.listar();

        assertEquals(respostaEsperada, resultado);
        verify(repository).findAll();
        verify(mapper).toListagemDto(entidades);
    }

    @Test
    void deveListarVazioQuandoNaoHaServicos() {
        when(repository.findAll()).thenReturn(List.of());
        when(mapper.toListagemDto(List.of())).thenReturn(List.of());

        List<ServicoResponseDTO> resultado = service.listar();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void devePesquisarPorIdERetornarEntidade() {
        Servico entidade = new Servico("Alinhamento", "Descrição", BigDecimal.TEN, 60, true);
        entidade.setId(1);
        when(repository.findById(1)).thenReturn(Optional.of(entidade));

        Servico resultado = service.pesquisarPorId(1);

        assertEquals(entidade, resultado);
    }

    @Test
    void devePesquisarPorIdLancarExcecaoComIdCorreto() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        ServicoNaoEncontradoException ex = assertThrows(
                ServicoNaoEncontradoException.class,
                () -> service.pesquisarPorId(99));

        assertNotNull(ex);
    }

    @Test
    void deveBuscarPorIdERetornarDto() {
        Servico entidade = new Servico("Alinhamento", "Descrição", BigDecimal.TEN, 60, true);
        entidade.setId(1);
        ServicoResponseDTO response = new ServicoResponseDTO("Alinhamento", "Descrição", BigDecimal.TEN, 60, true);

        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(mapper.toResponseDto(entidade)).thenReturn(response);

        ServicoResponseDTO resultado = service.buscarPorId(1);

        assertEquals(response, resultado);
    }

    @Test
    void deveAtualizarTodosOsCamposQuandoTodosInformados() {
        Servico entidade = new Servico("Alinhamento", "Descrição antiga", new BigDecimal("100.00"), 60, true);
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(
                "Novo nome", "Nova descrição", new BigDecimal("150.00"), 90, false);
        ServicoResponseDTO response = new ServicoResponseDTO(
                "Novo nome", "Nova descrição", new BigDecimal("150.00"), 90, false);

        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(response);

        ServicoResponseDTO resultado = service.atualizar(request, 1);

        assertEquals(response, resultado);
        assertEquals("Novo nome", entidade.getNome());
        assertEquals("Nova descrição", entidade.getDescricao());
        assertEquals(new BigDecimal("150.00"), entidade.getValorMaoDeObra());
        assertEquals(90, entidade.getTempoEstimadoMin());
        assertFalse(entidade.isAtivo());
    }

    @Test
    void deveAtualizarSomenteNome() {
        Servico entidade = new Servico("Nome antigo", "Descrição", BigDecimal.TEN, 60, true);
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(
                "Nome novo", null, null, null, null);

        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(
                new ServicoResponseDTO("Nome novo", "Descrição", BigDecimal.TEN, 60, true));

        service.atualizar(request, 1);

        assertEquals("Nome novo", entidade.getNome());
        assertEquals("Descrição", entidade.getDescricao());
        assertEquals(BigDecimal.TEN, entidade.getValorMaoDeObra());
        assertEquals(60, entidade.getTempoEstimadoMin());
        assertTrue(entidade.isAtivo());
    }

    @Test
    void deveAtualizarSomenteDescricao() {
        Servico entidade = new Servico("Nome", "Descrição antiga", BigDecimal.TEN, 60, true);
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(
                null, "Descrição nova", null, null, null);

        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(
                new ServicoResponseDTO("Nome", "Descrição nova", BigDecimal.TEN, 60, true));

        service.atualizar(request, 1);

        assertEquals("Descrição nova", entidade.getDescricao());
    }

    @Test
    void deveAtualizarSomenteTempoEstimado() {
        Servico entidade = new Servico("Nome", "Descrição", BigDecimal.TEN, 60, true);
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(
                null, null, null, 120, null);

        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(
                new ServicoResponseDTO("Nome", "Descrição", BigDecimal.TEN, 120, true));

        service.atualizar(request, 1);

        assertEquals(120, entidade.getTempoEstimadoMin());
    }

    @Test
    void deveAtualizarSomenteAtivo() {
        Servico entidade = new Servico("Nome", "Descrição", BigDecimal.TEN, 60, true);
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(
                null, null, null, null, false);

        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(
                new ServicoResponseDTO("Nome", "Descrição", BigDecimal.TEN, 60, false));

        service.atualizar(request, 1);

        assertFalse(entidade.isAtivo());
    }

    @Test
    void naoDeveAlterarNomeQuandoStringEmBranco() {
        Servico entidade = new Servico("Nome original", "Descrição", BigDecimal.TEN, 60, true);
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(
                "   ", null, null, null, null);

        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(
                new ServicoResponseDTO("Nome original", "Descrição", BigDecimal.TEN, 60, true));

        service.atualizar(request, 1);


        assertEquals("Nome original", entidade.getNome());
    }

    @Test
    void naoDeveAlterarDescricaoQuandoStringVazia() {
        Servico entidade = new Servico("Nome", "Descrição original", BigDecimal.TEN, 60, true);
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(
                null, "", null, null, null);

        when(repository.findById(1)).thenReturn(Optional.of(entidade));
        when(repository.save(entidade)).thenReturn(entidade);
        when(mapper.toResponseDto(entidade)).thenReturn(
                new ServicoResponseDTO("Nome", "Descrição original", BigDecimal.TEN, 60, true));

        service.atualizar(request, 1);

        assertEquals("Descrição original", entidade.getDescricao());
    }

    @Test
    void deveLancarExcecaoAoAtualizarServicoInexistente() {
        AtualizacaoServicoRequestDTO request = new AtualizacaoServicoRequestDTO(
                "Nome novo", null, null, null, null);

        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoEncontradoException.class,
                () -> service.atualizar(request, 99));

        verify(repository, never()).save(any());
    }



    @Test
    void deveLancarExcecaoAoExcluirServicoInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoEncontradoException.class,
                () -> service.deletar(99));

        verify(repository, never()).delete(any());
    }
}
