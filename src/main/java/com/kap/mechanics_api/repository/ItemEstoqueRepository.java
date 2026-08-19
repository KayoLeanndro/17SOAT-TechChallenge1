package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.ItemEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Integer> {
}
