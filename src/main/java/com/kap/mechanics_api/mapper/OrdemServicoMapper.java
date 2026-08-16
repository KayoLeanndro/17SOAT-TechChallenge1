package com.kap.mechanics_api.mapper;

import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.dto.ordemservico.CriacaoOrdemServicoRequestDTO;
import com.kap.mechanics_api.dto.ordemservico.OrdemServicoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdemServicoMapper {

    @Mapping(target = "id", ignore = true)
    OrdemServico toEntity(CriacaoOrdemServicoRequestDTO dto);

    OrdemServicoResponseDTO toResponseDto(OrdemServico ordemServico);

    List<OrdemServicoResponseDTO> toResponseDtoList(List<OrdemServico> ordensServico);
}
