package com.kap.mechanics_api.mapper;

import com.kap.mechanics_api.domain.Usuario;
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
    Usuario toEntity(CriacaoUsuarioRequestDTO dto);
    CriacaoUsuarioResponseDTO toResponseDto(Usuario usuario);
    List<ListagemUsuarioDTO> toListagemUsuarioDto(List<Usuario> usuarios);
    ListagemUsuarioDTO toListagemUsuarioResponseDto(Usuario usuario);
    AtualizacaoUsuarioResponseDTO toAtualizacaoUsuarioResponseDto(Usuario usuario);
}
