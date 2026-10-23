package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.UsuarioJpaEntity;
import com.kap.mechanics_api.core.usuario.domain.Usuario;   
import org.springframework.stereotype.Component;

@Component
public class UsuarioJpaMapper {

  public Usuario paraDominio(UsuarioJpaEntity e) {
    var usuario = new Usuario(
            e.getId(),
            e.getNome(),
            e.getLogin(),
            e.getSenhaHash(),
            e.getTipo()
    );
    usuario.setDataCriacao(e.getDataCriacao());
    return usuario;
}

   public UsuarioJpaEntity paraEntity(Usuario usuario) {
      var entity = new UsuarioJpaEntity(usuario.getId(), usuario.getNome(), usuario.getLogin(), usuario.getSenhaHash(), usuario.getTipo());
      entity.setDataCriacao(usuario.getDataCriacao());
      return entity;
   }

}