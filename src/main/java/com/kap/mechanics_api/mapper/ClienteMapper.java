package com.kap.mechanics_api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.dto.cliente.AtualizacaoClienteResponseDTO;
import com.kap.mechanics_api.dto.cliente.CriacaoClienteRequestDTO;
import com.kap.mechanics_api.dto.cliente.CriacaoClienteResponseDTO;
import com.kap.mechanics_api.dto.cliente.ListagemClienteResponseDTO;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
	
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    Cliente dtoToEntity(CriacaoClienteRequestDTO dto);
    CriacaoClienteResponseDTO entityToDto(Cliente cliente);
    
    List<ListagemClienteResponseDTO> listEntityToListDto (List<Cliente> cliente);
    ListagemClienteResponseDTO entityToListagemDto(Cliente cliente);
    AtualizacaoClienteResponseDTO entityToAtualizacaoDto(Cliente cliente);
}
