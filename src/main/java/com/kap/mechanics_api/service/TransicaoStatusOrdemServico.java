package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.exception.TransicaoStatusInvalidaException;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TransicaoStatusOrdemServico {

    private final StatusOrdemServicoRepository statusRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public TransicaoStatusOrdemServico(StatusOrdemServicoRepository repository,
                                       MovimentacaoEstoqueService movimentacaoEstoqueService){
        this.statusRepository = repository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @Transactional
    public void transicionar(OrdemServico os, StatusOrdemServicoEnum novoStatus) {
        boolean valida = transicoesValidas(os.getStatusOrdemServico().getNome())
                .contains(novoStatus);

        if (!valida) {
            throw new TransicaoStatusInvalidaException(
                    "Não é possível ir de " + os.getStatusOrdemServico().getNome() + " para " + novoStatus);
        }

        StatusOrdemServico status = statusRepository.findByNome(novoStatus.name())
                .orElseThrow(() -> new IllegalStateException("Status não cadastrado: " + novoStatus));

        os.setStatusOrdemServico(status);

        if (novoStatus == StatusOrdemServicoEnum.EM_EXECUCAO) {
            movimentacaoEstoqueService.baixarItensDaOrdemServico(os);
        }
    }

    private Set<StatusOrdemServicoEnum> transicoesValidas(String statusAtual) {
        return switch (StatusOrdemServicoEnum.valueOf(statusAtual)) {
            case RECEBIDA -> Set.of(StatusOrdemServicoEnum.EM_DIAGNOSTICO);
            case EM_DIAGNOSTICO -> Set.of(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO);
            case AGUARDANDO_APROVACAO -> Set.of(StatusOrdemServicoEnum.EM_EXECUCAO);
            case EM_EXECUCAO -> Set.of(StatusOrdemServicoEnum.FINALIZADA);
            case FINALIZADA -> Set.of(StatusOrdemServicoEnum.ENTREGUE);
            case ENTREGUE -> Set.of();
        };
    }

}
