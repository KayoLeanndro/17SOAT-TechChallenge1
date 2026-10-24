package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.UsuarioJpaEntity;
import com.kap.mechanics_api.core.movimentacaoestoque.gateway.ResponsavelMovimentacaoGateway;
import com.kap.mechanics_api.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ResponsavelMovimentacaoJpaGateway implements ResponsavelMovimentacaoGateway {

    private final UsuarioRepository usuarioRepository;

    public ResponsavelMovimentacaoJpaGateway(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Integer> buscarIdPorLogin(String login) {
        return usuarioRepository.findByLogin(login).map(UsuarioJpaEntity::getId);
    }
}
