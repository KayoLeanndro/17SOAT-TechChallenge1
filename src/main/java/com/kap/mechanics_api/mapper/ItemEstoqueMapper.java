package com.kap.mechanics_api.mapper;

import com.kap.mechanics_api.domain.ItemEstoque;
import com.kap.mechanics_api.dto.itemestoque.AtualizacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.CriacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.ItemEstoqueResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemEstoqueMapper {

    @Mapping(target = "id", ignore = true)
    ItemEstoque toEntity(CriacaoItemEstoqueRequestDTO dto);

    ItemEstoqueResponseDTO toResponseDto(ItemEstoque itemEstoque);

    List<ItemEstoqueResponseDTO> toResponseDtoList(List<ItemEstoque> itens);
}
