package com.kap.mechanics_api.repository;
import com.kap.mechanics_api.domain.OrcamentoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrcamentoItemRepository extends JpaRepository<OrcamentoItem, Integer> { List<OrcamentoItem> findByOrcamento_Id(Integer orcamentoId); }
