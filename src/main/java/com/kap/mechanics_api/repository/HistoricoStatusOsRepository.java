package com.kap.mechanics_api.repository;

import com.kap.mechanics_api.domain.HistoricoStatusOs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoricoStatusOsRepository extends JpaRepository<HistoricoStatusOs, Long> {

    Optional<HistoricoStatusOs> findByOrdemServico_IdAndDataHoraFimIsNull(Integer ordemServicoId);

    List<HistoricoStatusOs> findByOrdemServico_IdOrderByDataHoraInicioAsc(Integer ordemServicoId);

    @Query(value = """
            SELECT os.id AS "ordemServicoId",
                   h.data_hora_inicio AS "dataHoraInicio",
                   h.data_hora_fim AS "dataHoraFim"
              FROM historico_status_os h
              JOIN status_ordem_servico st ON st.id = h.status_id
              JOIN ordem_servico os ON os.id = h.ordem_servico_id
              JOIN orcamento_servico ors ON ors.orcamento_id = os.orcamento_id
              JOIN servico s ON s.id = ors.servico_id
             WHERE st.nome = 'EM_EXECUCAO'
               AND h.data_hora_fim IS NOT NULL
               AND s.id = :servicoId
            """, nativeQuery = true)
    List<ExecucaoServicoProjection> buscarExecucoesFinalizadasPorServico(
            @Param("servicoId") Integer servicoId);
}
