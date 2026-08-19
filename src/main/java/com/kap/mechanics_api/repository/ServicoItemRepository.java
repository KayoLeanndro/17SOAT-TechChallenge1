package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.ServicoItem;
import com.kap.mechanics_api.domain.ServicoItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoItemRepository extends JpaRepository<ServicoItem, ServicoItemId> {

    List<ServicoItem> findByServico_Id(Integer servicoId);
}