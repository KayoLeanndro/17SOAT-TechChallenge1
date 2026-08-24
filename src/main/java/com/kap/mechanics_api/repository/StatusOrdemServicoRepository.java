package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.StatusOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusOrdemServicoRepository extends JpaRepository<StatusOrdemServico, Integer> {
    Optional<StatusOrdemServico> findByNome(String nome);
}
