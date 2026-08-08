package com.kap.mechanics_api.mapper;


import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.dto.servico.*;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServicoMapper {

    @Mapping(target = "id", ignore = true)
    Servico toEntity(@Valid CriacaoServicoRequestDTO dto);
    CriacaoServicoResponseDTO toResponseDto(Servico servico);
    List<ListagemServicoResponseDTO> toListagemDto(List<Servico> servicos);
    ListagemServicoResponseDTO toListagemServicoResponseDto(Servico servico);
    AtualizacaoServicoResponseDTO toAtualizacaoServicoResponseDto(Servico servico);
    void atualizarServico(
            AtualizacaoServicoRequestDTO dto,
            @MappingTarget Servico servico
    );


}


