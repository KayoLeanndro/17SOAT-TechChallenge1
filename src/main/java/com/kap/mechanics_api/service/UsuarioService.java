package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.dto.usuario.*;
import com.kap.mechanics_api.enums.TipoUsuario;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.UsuarioNaoEncontradoException;
import com.kap.mechanics_api.mapper.UsuarioMapper;
import com.kap.mechanics_api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioMapper mapper, UsuarioRepository repository){
        this.usuarioMapper= mapper;
        this.usuarioRepository = repository;
    }

    public CriacaoUsuarioResponseDTO cadastrar(CriacaoUsuarioRequestDTO dto) {
        Usuario usuario = usuarioMapper.toEntity(dto);
        return usuarioMapper.toResponseDto(usuarioRepository.save(usuario));
    }

    public Usuario buscarPorId(Integer id){
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    public ListagemUsuarioDTO pesquisarPorId(Integer id) {
        return usuarioMapper.toListagemUsuarioResponseDto(buscarPorId(id));
    }

    public List<ListagemUsuarioDTO> listar() {
        return usuarioMapper.toListagemUsuarioDto(usuarioRepository.findAll());
    }

    public AtualizacaoUsuarioResponseDTO atualizar(Integer id, AtualizacaoUsuarioRequestDTO dto) {

        if(!dto.temAoMenosUmCampoPreenchido()){
            throw new NenhumCampoInformadoException(dto.getClass());
        }

        Usuario usuario = buscarPorId(id);

        if(StringUtils.hasText(dto.nome())){
            usuario.setNome(dto.nome());
        }

        if(StringUtils.hasText(dto.login())){
            usuario.setLogin(dto.login());
        }

        if(StringUtils.hasText(dto.senha())){
            usuario.setSenhaHash(dto.senha());
        }

        if(StringUtils.hasText(dto.tipo())){
            usuario.setTipo(TipoUsuario.valueOf(dto.tipo()));
        }

        return usuarioMapper.toAtualizacaoUsuarioResponseDto(usuarioRepository.save(usuario));
    }

    public void deletar(Integer id) {

        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);

    }
}
