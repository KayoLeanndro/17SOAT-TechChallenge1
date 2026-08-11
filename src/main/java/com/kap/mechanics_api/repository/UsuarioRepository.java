package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
