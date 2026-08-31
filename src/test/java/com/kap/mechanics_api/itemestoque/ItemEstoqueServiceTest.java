package com.kap.mechanics_api.itemestoque;

import com.kap.mechanics_api.domain.ItemEstoque;
import com.kap.mechanics_api.dto.itemestoque.AtualizacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.CriacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.ItemEstoqueResponseDTO;
import com.kap.mechanics_api.enums.TipoItemEstoque;
import com.kap.mechanics_api.exception.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.mapper.ItemEstoqueMapper;
import com.kap.mechanics_api.repository.ItemEstoqueRepository;
import com.kap.mechanics_api.service.ItemEstoqueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemEstoqueServiceTest {

    @Mock private ItemEstoqueRepository itemEstoqueRepository;
    @Mock private ItemEstoqueMapper itemEstoqueMapper;

    @InjectMocks private ItemEstoqueService service;

    @Test
    void deveCadastrarItem() {
        CriacaoItemEstoqueRequestDTO dto = new CriacaoItemEstoqueRequestDTO(
                "Filtro", "Filtro de óleo", TipoItemEstoque.PECA, new BigDecimal("25.00"), 2, true);
        ItemEstoque entidade = item(null, "Filtro");
        ItemEstoque salvo = item(1, "Filtro");
        ItemEstoqueResponseDTO response = response(1, "Filtro");

        when(itemEstoqueMapper.toEntity(dto)).thenReturn(entidade);
        when(itemEstoqueRepository.save(entidade)).thenReturn(salvo);
        when(itemEstoqueMapper.toResponseDto(salvo)).thenReturn(response);

        assertSame(response, service.cadastrar(dto));
        verify(itemEstoqueRepository).save(entidade);
    }

    @Test
    void deveListarItens() {
        List<ItemEstoque> itens = List.of(item(1, "Filtro"), item(2, "Vela"));
        List<ItemEstoqueResponseDTO> responses = List.of(response(1, "Filtro"), response(2, "Vela"));
        when(itemEstoqueRepository.findAll()).thenReturn(itens);
        when(itemEstoqueMapper.toResponseDtoList(itens)).thenReturn(responses);

        assertEquals(2, service.listar().size());
    }

    @Test
    void deveBuscarPorIdQuandoExiste() {
        ItemEstoque item = item(1, "Filtro");
        ItemEstoqueResponseDTO response = response(1, "Filtro");
        when(itemEstoqueRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemEstoqueMapper.toResponseDto(item)).thenReturn(response);

        assertSame(response, service.buscarPorId(1));
    }

    @Test
    void devePesquisarPorIdRetornandoEntidade() {
        ItemEstoque item = item(1, "Filtro");
        when(itemEstoqueRepository.findById(1)).thenReturn(Optional.of(item));

        assertSame(item, service.pesquisarPorId(1));
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExiste() {
        when(itemEstoqueRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class, () -> service.pesquisarPorId(99));
    }

    @Test
    void deveRejeitarAtualizacaoSemNenhumCampoPreenchido() {
        AtualizacaoItemEstoqueRequestDTO dto =
                new AtualizacaoItemEstoqueRequestDTO(null, null, null, null, null, null);

        assertThrows(NenhumCampoInformadoException.class, () -> service.atualizar(1, dto));
        verify(itemEstoqueRepository, never()).findById(1);
    }

    @Test
    void deveAtualizarTodosOsCamposInformados() {
        ItemEstoque item = item(1, "Antigo");
        item.setDescricao("Descrição antiga");
        item.setValorUnitario(new BigDecimal("10.00"));
        item.setQuantidadeMinima(1);
        item.setAtivo(true);
        AtualizacaoItemEstoqueRequestDTO dto = new AtualizacaoItemEstoqueRequestDTO(
                "Novo", "Descrição nova", TipoItemEstoque.INSUMO, new BigDecimal("42.00"), 5, false);
        ItemEstoqueResponseDTO response = response(1, "Novo");

        when(itemEstoqueRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemEstoqueRepository.save(item)).thenReturn(item);
        when(itemEstoqueMapper.toResponseDto(item)).thenReturn(response);

        assertSame(response, service.atualizar(1, dto));
        assertEquals("Novo", item.getNome());
        assertEquals("Descrição nova", item.getDescricao());
        assertEquals(TipoItemEstoque.INSUMO, item.getTipoItemEstoque());
        assertEquals(0, new BigDecimal("42.00").compareTo(item.getValorUnitario()));
        assertEquals(5, item.getQuantidadeMinima());
        assertEquals(false, item.isAtivo());
    }

    @Test
    void deveAtualizarApenasOCampoInformado() {
        ItemEstoque item = item(1, "Antigo");
        item.setValorUnitario(new BigDecimal("10.00"));
        AtualizacaoItemEstoqueRequestDTO dto =
                new AtualizacaoItemEstoqueRequestDTO("Somente nome", null, null, null, null, null);

        when(itemEstoqueRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemEstoqueRepository.save(item)).thenReturn(item);
        when(itemEstoqueMapper.toResponseDto(item)).thenReturn(response(1, "Somente nome"));

        service.atualizar(1, dto);

        assertEquals("Somente nome", item.getNome());
        assertEquals(0, new BigDecimal("10.00").compareTo(item.getValorUnitario()));
    }

    @Test
    void deveDeletarItemExistente() {
        ItemEstoque item = item(1, "Filtro");
        when(itemEstoqueRepository.findById(1)).thenReturn(Optional.of(item));

        service.deletar(1);

        verify(itemEstoqueRepository).delete(item);
    }

    @Test
    void deveLancarExcecaoAoDeletarItemInexistente() {
        when(itemEstoqueRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ItemEstoqueNaoEncontradoException.class, () -> service.deletar(99));
        verify(itemEstoqueRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    private ItemEstoque item(Integer id, String nome) {
        ItemEstoque item = new ItemEstoque();
        item.setId(id);
        item.setNome(nome);
        item.setTipoItemEstoque(TipoItemEstoque.PECA);
        return item;
    }

    private ItemEstoqueResponseDTO response(Integer id, String nome) {
        return new ItemEstoqueResponseDTO(id, nome, "descricao", TipoItemEstoque.PECA,
                new BigDecimal("10.00"), 0, 1, true);
    }
}
