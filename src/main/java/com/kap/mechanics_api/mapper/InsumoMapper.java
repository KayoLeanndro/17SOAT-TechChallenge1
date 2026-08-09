package com.kap.mechanics_api.mapper;

import com.kap.mechanics_api.domain.Insumo;
import com.kap.mechanics_api.dto.insumo.CriacaoInsumoRequestDTO;
import com.kap.mechanics_api.dto.insumo.InsumoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InsumoMapper {
    @Mapping(target = "id", ignore = true)
    Insumo toEntity(CriacaoInsumoRequestDTO dto);
    InsumoResponseDTO toResponseDto(Insumo insumo);
    List<InsumoResponseDTO> toResponseDtoList(List<Insumo> insumos);
}
