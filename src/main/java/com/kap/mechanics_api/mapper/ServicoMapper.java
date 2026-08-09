package com.kap.mechanics_api.mapper;


import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.dto.servico.*;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServicoMapper {

    @Mapping(target = "id", ignore = true)
    Servico toEntity(@Valid CriacaoServicoRequestDTO dto);
    ServicoResponseDTO toResponseDto(Servico servico);
    List<ServicoResponseDTO> toListagemDto(List<Servico> servicos);
    ServicoResponseDTO toListagemServicoResponseDto(Servico servico);
    ServicoResponseDTO toAtualizacaoServicoResponseDto(Servico servico);
}


