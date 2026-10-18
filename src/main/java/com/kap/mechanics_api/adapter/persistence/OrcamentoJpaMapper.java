package com.kap.mechanics_api.adapter.persistence;

import com.kap.mechanics_api.adapter.persistence.entity.OrcamentoItemJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.OrcamentoJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.OrcamentoServicoJpaEntity;
import com.kap.mechanics_api.core.orcamento.domain.ItemOrcamento;
import com.kap.mechanics_api.core.orcamento.domain.Orcamento;
import com.kap.mechanics_api.core.orcamento.domain.ServicoOrcado;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrcamentoJpaMapper {

    public Orcamento paraDominio(OrcamentoJpaEntity e, List<OrcamentoItemJpaEntity> itens,
                                 List<OrcamentoServicoJpaEntity> servicos) {
        return Orcamento.reconstruir(e.getId(), e.getCliente().getId(), e.getVeiculo().getId(), e.getValorTotal(),
                e.getStatusOrcamento(), e.getDataCriacao(), e.getDataResposta(),
                itens.stream().map(this::itemParaDominio).toList(),
                servicos.stream().map(s -> new ServicoOrcado(s.getServico().getId(), s.getValorCobrado())).toList());
    }

    private ItemOrcamento itemParaDominio(OrcamentoItemJpaEntity i) {
        return ItemOrcamento.reconstruir(i.getId(),
                i.getServico() == null ? null : i.getServico().getId(),
                i.getItemEstoque() == null ? null : i.getItemEstoque().getId(),
                i.getQuantidade(), i.getValorUnitarioCobrado());
    }
}
