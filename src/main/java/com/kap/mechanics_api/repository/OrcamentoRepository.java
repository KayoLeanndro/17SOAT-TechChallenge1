package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Integer> {
}
