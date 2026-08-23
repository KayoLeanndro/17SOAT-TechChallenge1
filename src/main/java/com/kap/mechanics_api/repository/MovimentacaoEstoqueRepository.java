package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.MovimentacaoEstoque;
import com.kap.mechanics_api.enums.TipoMovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findAllByOrderByDataHoraDesc();

    List<MovimentacaoEstoque> findByItemEstoque_IdOrderByDataHoraDesc(Integer itemEstoqueId);

    List<MovimentacaoEstoque> findByOrdemServico_IdOrderByDataHoraDesc(Long ordemServicoId);

    List<MovimentacaoEstoque> findByTipoOrderByDataHoraDesc(TipoMovimentacaoEstoque tipo);

    List<MovimentacaoEstoque> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim);

    List<MovimentacaoEstoque> findByItemEstoque_IdOrderByDataHoraAscIdAsc(Integer itemEstoqueId);
}
