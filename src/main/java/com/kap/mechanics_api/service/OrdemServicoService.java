package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.Orcamento;
import com.kap.mechanics_api.domain.OrdemServico;
import com.kap.mechanics_api.domain.StatusOrdemServico;
import com.kap.mechanics_api.domain.Usuario;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.exception.OrcamentoNaoAprovadoException;
import com.kap.mechanics_api.exception.OrcamentoNaoEncontradoException;
import com.kap.mechanics_api.exception.OrdemServicoJaExisteException;
import com.kap.mechanics_api.exception.OrdemServicoNaoEncontradaException;
import com.kap.mechanics_api.exception.UsuarioNaoEncontradoException;
import com.kap.mechanics_api.repository.OrcamentoRepository;
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
    private final OrcamentoRepository orcamentoRepository;
    private final TransicaoStatusOrdemServico transicaoStatusOrdemServico;
    private final UsuarioRepository usuarioRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               StatusOrdemServicoRepository statusOrdemServicoRepository,
                               OrcamentoRepository orcamentoRepository,
                               TransicaoStatusOrdemServico transicaoStatusOrdemServico,
                               UsuarioRepository usuarioRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.statusOrdemServicoRepository = statusOrdemServicoRepository;
        this.orcamentoRepository = orcamentoRepository;
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
    public OrdemServico criarParaOrcamentoPendente(Integer orcamentoId, String usuarioLogin) {
        Usuario usuario = usuarioRepository.findByLogin(usuarioLogin)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioLogin));
        Orcamento orcamento = buscarOrcamento(orcamentoId);

        if (orcamento.getStatusOrcamento() != StatusOrcamento.PENDENTE) {
            throw new IllegalArgumentException("A OS inicial só pode ser criada para orçamento pendente.");
        }
        return criarOrdemServico(orcamento, usuario);
    }

    @Transactional
    public OrdemServico gerarOrdemServico(Integer orcamentoId, Usuario usuario) {
        Orcamento orcamento = buscarOrcamento(orcamentoId);

        if (orcamento.getStatusOrcamento() != StatusOrcamento.APROVADO) {
            throw new OrcamentoNaoAprovadoException("Orçamento precisa estar aprovado para gerar OS");
        }
        return criarOrdemServico(orcamento, usuario);
    }

    @Transactional
    public OrdemServico finalizarPorOrcamento(Integer orcamentoId) {
        OrdemServico ordemServico = ordemServicoRepository.findByOrcamento_Id(orcamentoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(orcamentoId));
        StatusOrdemServico statusFinalizado = statusOrdemServicoRepository
                .findByNome(StatusOrdemServicoEnum.FINALIZADA.name())
                .orElseThrow(() -> new IllegalStateException("Status FINALIZADA não cadastrado"));

        ordemServico.setStatusOrdemServico(statusFinalizado);
        return ordemServicoRepository.save(ordemServico);
    }

    @Transactional
    public OrdemServico iniciarDiagnosticoPorOrcamento(Integer orcamentoId) {
        OrdemServico ordemServico = ordemServicoRepository.findByOrcamento_Id(orcamentoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(orcamentoId));
        return transicionarStatus(ordemServico.getId(), StatusOrdemServicoEnum.EM_DIAGNOSTICO);
    }

    private OrdemServico criarOrdemServico(Orcamento orcamento, Usuario usuario) {
        if (ordemServicoRepository.existsByOrcamentoId(orcamento.getId())) {
            throw new OrdemServicoJaExisteException("Este orçamento já possui uma OS gerada");
        }

        StatusOrdemServico statusInicial = statusOrdemServicoRepository
                .findByNome(StatusOrdemServicoEnum.AGUARDANDO_APROVACAO.name())
                .orElseThrow(() -> new IllegalStateException("Status AGUARDANDO_APROVACAO não cadastrado"));

        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setOrcamento(orcamento);
        ordemServico.setUsuarioAtendente(usuario);
        ordemServico.setStatusOrdemServico(statusInicial);
        ordemServico.setDataAbertura(LocalDateTime.now());

        return ordemServicoRepository.save(ordemServico);
    }

    private Orcamento buscarOrcamento(Integer orcamentoId) {
        return orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento não encontrado"));
    }

    @Transactional
    public OrdemServico transicionarStatus(Integer ordemServicoId, StatusOrdemServicoEnum novoStatus) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));

        transicaoStatusOrdemServico.transicionar(ordemServico, novoStatus);
        return ordemServicoRepository.save(ordemServico);
    }
}
