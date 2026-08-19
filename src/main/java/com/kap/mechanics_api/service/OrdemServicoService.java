package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrdemServico;
//import com.kap.mechanics_api.dto.ordemservico.AtualizacaoOrdemServicoRequestDTO;
//import com.kap.mechanics_api.dto.ordemservico.CriacaoOrdemServicoRequestDTO;
//import com.kap.mechanics_api.dto.ordemservico.OrdemServicoResponseDTO;
//import com.kap.mechanics_api.enums.StatusOrdemServico;
//import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
//import com.kap.mechanics_api.exception.OrcamentoNaoEncontradoException;
//import com.kap.mechanics_api.exception.OrdemServicoNaoEncontradaException;
//import com.kap.mechanics_api.mapper.OrdemServicoMapper;
//import com.kap.mechanics_api.repository.OrcamentoRepository;
//import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.exception.OrcamentoNaoAprovadoException;
import com.kap.mechanics_api.exception.OrdemServicoJaExisteException;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final StatusOrdemServicoRepository statusOrdemServicoRepository;
    private final OrcamentoService orcamentoService;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository, StatusOrdemServicoRepository statusOrdemServicoRepository
                               , OrcamentoService orcamentoService){
        this.orcamentoService = orcamentoService;
        this.ordemServicoRepository = ordemServicoRepository;
        this.statusOrdemServicoRepository = statusOrdemServicoRepository;
    }

    @Transactional
    public OrdemServico gerarOrdemServico(Long orcamentoId, Usuario usuario){

        var orcamento = orcamentoService.pesquisarPorId(orcamentoId.intValue());

        if (!orcamento.getStatusOrcamento().equals(StatusOrcamento.APROVADO)) {
            throw new OrcamentoNaoAprovadoException("Orçamento precisa estar aprovado para gerar OS");
        }
        if (ordemServicoRepository.existsByOrcamentoId(orcamentoId)) {
            throw new OrdemServicoJaExisteException("Este orçamento já possui uma OS gerada");
        }

        StatusOrdemServico statusInicial = statusOrdemServicoRepository
                .findByNome(StatusOrdemServicoEnum.RECEBIDA.name())
                .orElseThrow(() -> new IllegalStateException("Status RECEBIDA não cadastrado"));

        OrdemServico os = new OrdemServico();
        os.setOrcamento(orcamento);
        os.setUsuarioAtendente(usuario);
        os.setStatusOrdemServico(statusInicial);
        os.setDataAbertura(LocalDateTime.now());

        return ordemServicoRepository.save(os);

    }

}
