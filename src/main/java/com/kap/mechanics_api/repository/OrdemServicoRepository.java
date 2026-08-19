package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    boolean existsByOrcamentoId(Long orcamentoId);
}
