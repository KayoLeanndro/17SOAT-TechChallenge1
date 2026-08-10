package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.Peca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PecaRepository extends JpaRepository<Peca, Integer> {
}
