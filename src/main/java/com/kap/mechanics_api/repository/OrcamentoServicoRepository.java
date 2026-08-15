package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.OrcamentoServico;
import com.kap.mechanics_api.domain.OrcamentoServicoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrcamentoServicoRepository extends JpaRepository<OrcamentoServico, OrcamentoServicoId> {
}
