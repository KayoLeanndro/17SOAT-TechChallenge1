package com.kap.mechanics_api.repository;
import com.kap.mechanics_api.domain.OrdemServicoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrdemServicoItemRepository extends JpaRepository<OrdemServicoItem, Integer> { List<OrdemServicoItem> findByOrdemServico_Id(Integer ordemServicoId); }
