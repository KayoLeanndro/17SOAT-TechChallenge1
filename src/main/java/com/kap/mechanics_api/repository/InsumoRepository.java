package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
}
