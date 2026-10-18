package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.HistoricoStatusOsJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.OrdemServicoItemJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.OrdemServicoJpaEntity;
import com.kap.mechanics_api.core.ordemservico.domain.HistoricoDeStatus;
import com.kap.mechanics_api.core.ordemservico.domain.ItemOrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServico;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServicoResumo;
import com.kap.mechanics_api.core.ordemservico.domain.StatusOrdemServico;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdemServicoJpaMapper {

    public OrdemServico paraDominio(OrdemServicoJpaEntity e, List<HistoricoStatusOsJpaEntity> historico,
                                    List<OrdemServicoItemJpaEntity> itens) {
        return OrdemServico.reconstruir(e.getId(), e.getOrcamento().getId(), e.getUsuarioAtendente().getId(),
                status(e), e.getDataAbertura(), e.getDataEntrega(),
                historico.stream().map(this::historicoParaDominio).toList(),
                itens.stream().map(this::itemParaDominio).toList());
    }

    public OrdemServicoResumo paraResumo(OrdemServicoJpaEntity e) {
        return new OrdemServicoResumo(e.getId(), e.getOrcamento().getId(), status(e), e.getDataAbertura(),
                e.getDataEntrega());
    }

    private StatusOrdemServico status(OrdemServicoJpaEntity e) {
        return StatusOrdemServico.valueOf(e.getStatusOrdemServico().getNome());
    }

    private HistoricoDeStatus historicoParaDominio(HistoricoStatusOsJpaEntity h) {
        return HistoricoDeStatus.reconstruir(h.getId(), StatusOrdemServico.valueOf(h.getStatus().getNome()),
                h.getDataHoraInicio(), h.getDataHoraFim());
    }

    private ItemOrdemServico itemParaDominio(OrdemServicoItemJpaEntity i) {
        return ItemOrdemServico.reconstruir(i.getId(),
                i.getOrcamentoItem() == null ? null : i.getOrcamentoItem().getId(),
                i.getServico() == null ? null : i.getServico().getId(),
                i.getItemEstoque() == null ? null : i.getItemEstoque().getId(),
                i.getQuantidade(), i.getValorUnitarioCobrado());
    }
}
