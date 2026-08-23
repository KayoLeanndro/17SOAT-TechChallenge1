package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    boolean existsByOrcamentoId(Integer orcamentoId);

    Optional<OrdemServico> findByOrcamento_Id(Integer orcamentoId);
}
