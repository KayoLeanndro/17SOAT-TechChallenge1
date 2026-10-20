package com.kap.mechanics_api.mapper;

import com.kap.mechanics_api.adapter.persistence.entity.UsuarioJpaEntity;
import com.kap.mechanics_api.dto.usuario.AtualizacaoUsuarioResponseDTO;
import com.kap.mechanics_api.dto.usuario.CriacaoUsuarioRequestDTO;
import com.kap.mechanics_api.dto.usuario.CriacaoUsuarioResponseDTO;
import com.kap.mechanics_api.dto.usuario.ListagemUsuarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    UsuarioJpaEntity toEntity(CriacaoUsuarioRequestDTO dto);
    CriacaoUsuarioResponseDTO toResponseDto(UsuarioJpaEntity usuario);
    List<ListagemUsuarioDTO> toListagemUsuarioDto(List<UsuarioJpaEntity> usuarios);
    ListagemUsuarioDTO toListagemUsuarioResponseDto(UsuarioJpaEntity usuario);
    AtualizacaoUsuarioResponseDTO toAtualizacaoUsuarioResponseDto(UsuarioJpaEntity usuario);
}
