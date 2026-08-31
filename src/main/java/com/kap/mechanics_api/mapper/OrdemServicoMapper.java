package com.kap.mechanics_api.mapper;

import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.dto.ordemservico.ListagemOrdemServicoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdemServicoMapper {

    @Mapping(target = "orcamentoId", source = "orcamento.id")
    @Mapping(target = "status", source = "statusOrdemServico.nome")
    ListagemOrdemServicoResponseDTO toListagemResponseDto(OrdemServico ordemServico);

    List<ListagemOrdemServicoResponseDTO> toListagemResponseDtoList(List<OrdemServico> ordens);
}
