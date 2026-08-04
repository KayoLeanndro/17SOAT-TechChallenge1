package com.kap.mechanics_api.mapper;

import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.dto.AtualizacaoVeiculoResponseDTO;
import com.kap.mechanics_api.dto.CriacaoVeiculoRequestDTO;
import com.kap.mechanics_api.dto.CriacaoVeiculoResponseDTO;
import com.kap.mechanics_api.dto.ListagemVeiculoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VeiculoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    Veiculo toEntity(CriacaoVeiculoRequestDTO dto);
    CriacaoVeiculoResponseDTO toResponseDto(Veiculo veiculo);
    List<ListagemVeiculoResponseDTO> toListagemDto(List<Veiculo> veiculos);
    ListagemVeiculoResponseDTO toListagemVeiculoResponseDto(Veiculo veiculo);
    AtualizacaoVeiculoResponseDTO toAtualizacaoVeiculoResponseDto(Veiculo veiculo);

}
