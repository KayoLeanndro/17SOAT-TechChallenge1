package com.kap.mechanics_api.mapper;

import com.kap.mechanics_api.domain.Peca;
import com.kap.mechanics_api.dto.peca.CriacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.PecaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PecaMapper {

    @Mapping(target = "id", ignore = true)
    Peca toEntity(CriacaoPecaRequestDTO dto);

    PecaResponseDTO toResponseDto(Peca peca);

    List<PecaResponseDTO> toResponseDtoList(List<Peca> pecas);
}
