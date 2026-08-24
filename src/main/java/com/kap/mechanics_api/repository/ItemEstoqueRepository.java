package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.ItemEstoque;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select item from ItemEstoque item where item.id = :id")
    Optional<ItemEstoque> findByIdForUpdate(@Param("id") Integer id);
}
