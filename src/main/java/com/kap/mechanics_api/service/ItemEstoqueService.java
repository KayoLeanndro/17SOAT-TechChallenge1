package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.ItemEstoque;
import com.kap.mechanics_api.dto.itemestoque.AtualizacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.CriacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.ItemEstoqueResponseDTO;
import com.kap.mechanics_api.exception.ItemEstoqueNaoEncontradoException;
import com.kap.mechanics_api.exception.MovimentacaoEstoqueObrigatoriaException;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.mapper.ItemEstoqueMapper;
import com.kap.mechanics_api.repository.ItemEstoqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ItemEstoqueService {

    private final ItemEstoqueRepository itemEstoqueRepository;
    private final ItemEstoqueMapper itemEstoqueMapper;

    public ItemEstoqueService(ItemEstoqueRepository itemEstoqueRepository, ItemEstoqueMapper itemEstoqueMapper) {
        this.itemEstoqueRepository = itemEstoqueRepository;
        this.itemEstoqueMapper = itemEstoqueMapper;
    }

    public ItemEstoqueResponseDTO cadastrar(CriacaoItemEstoqueRequestDTO dto) {
        return itemEstoqueMapper.toResponseDto(itemEstoqueRepository.save(itemEstoqueMapper.toEntity(dto)));
    }

    public List<ItemEstoqueResponseDTO> listar() {
        return itemEstoqueMapper.toResponseDtoList(itemEstoqueRepository.findAll());
    }

    public ItemEstoqueResponseDTO buscarPorId(Integer id) {
        return itemEstoqueMapper.toResponseDto(pesquisarPorId(id));
    }

    public ItemEstoque pesquisarPorId(Integer id) {
        return itemEstoqueRepository.findById(id)
                .orElseThrow(() -> new ItemEstoqueNaoEncontradoException(id));
    }

    public ItemEstoqueResponseDTO atualizar(Integer id, AtualizacaoItemEstoqueRequestDTO dto) {
        if (!dto.temAoMenosUmCampoPreenchido()) {
            throw new NenhumCampoInformadoException(AtualizacaoItemEstoqueRequestDTO.class);
        }

        ItemEstoque item = pesquisarPorId(id);
        if (StringUtils.hasText(dto.nome())) item.setNome(dto.nome());
        if (StringUtils.hasText(dto.descricao())) item.setDescricao(dto.descricao());
        if (dto.tipoItemEstoque() != null) item.setTipoItemEstoque(dto.tipoItemEstoque());
        if (dto.valorUnitario() != null) item.setValorUnitario(dto.valorUnitario());
        if (dto.quantidadeMinima() != null) item.setQuantidadeMinima(dto.quantidadeMinima());
        if (dto.ativo() != null) item.setAtivo(dto.ativo());

        return itemEstoqueMapper.toResponseDto(itemEstoqueRepository.save(item));
    }

    public void deletar(Integer id) {
        itemEstoqueRepository.delete(pesquisarPorId(id));
    }
}
