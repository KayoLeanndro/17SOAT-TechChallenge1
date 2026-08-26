package com.kap.mechanics_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kap.mechanics_api.domain.Cliente;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
}
