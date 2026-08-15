package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.ServicoPeca;
import com.kap.mechanics_api.domain.ServicoPecaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoPecaRepository extends JpaRepository<ServicoPeca, ServicoPecaId> {

    List<ServicoPeca> findByServico_Id(Integer servicoId);

}
