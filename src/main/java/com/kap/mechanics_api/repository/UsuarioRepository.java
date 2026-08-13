package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);
}
