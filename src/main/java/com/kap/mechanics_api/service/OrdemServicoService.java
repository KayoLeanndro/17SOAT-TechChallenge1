package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.exception.OrcamentoNaoAprovadoException;
import com.kap.mechanics_api.exception.OrdemServicoJaExisteException;
import com.kap.mechanics_api.exception.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.exception.UsuarioNaoEncontradoException;
import com.kap.mechanics_api.repository.OrdemServicoRepository;
import com.kap.mechanics_api.repository.StatusOrdemServicoRepository;
import com.kap.mechanics_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final StatusOrdemServicoRepository statusOrdemServicoRepository;
    private final OrcamentoService orcamentoService;
    private final TransicaoStatusOrdemServico transicaoStatusOrdemServico;
    private final UsuarioRepository usuarioRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository, StatusOrdemServicoRepository statusOrdemServicoRepository
                               , OrcamentoService orcamentoService,
                               TransicaoStatusOrdemServico transicaoStatusOrdemServico,
                               UsuarioRepository usuarioRepository){
        this.orcamentoService = orcamentoService;
        this.ordemServicoRepository = ordemServicoRepository;
        this.statusOrdemServicoRepository = statusOrdemServicoRepository;
        this.transicaoStatusOrdemServico = transicaoStatusOrdemServico;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public OrdemServico gerarOrdemServico(Integer orcamentoId, String usuarioLogin) {
        Usuario usuario = usuarioRepository.findByLogin(usuarioLogin)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioLogin));
        return gerarOrdemServico(orcamentoId, usuario);
    }

    @Transactional
    public OrdemServico gerarOrdemServico(Integer orcamentoId, Usuario usuario){

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

    @Transactional
    public OrdemServico transicionarStatus(Integer ordemServicoId, StatusOrdemServicoEnum novoStatus) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));

        transicaoStatusOrdemServico.transicionar(ordemServico, novoStatus);
        return ordemServicoRepository.save(ordemServico);
    }

}
