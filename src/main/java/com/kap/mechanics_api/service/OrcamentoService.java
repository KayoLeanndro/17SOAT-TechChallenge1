package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.*;
import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.exception.OrcamentoNaoEncontradoException;
import com.kap.mechanics_api.repository.OrcamentoRepository;
import com.kap.mechanics_api.repository.OrcamentoServicoRepository;
import com.kap.mechanics_api.repository.ServicoItemRepository;
import com.kap.mechanics_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    private final ServicoService servicoService;
    private final OrdemServicoService ordemServicoService;
    private final ServicoItemRepository servicoItemRepository;
    private final OrcamentoServicoRepository orcamentoServicoRepository;
    private final UsuarioRepository usuarioRepository;


    public OrcamentoService(OrcamentoRepository repository, ClienteService clienteService, VeiculoService veiculoService,
                            ServicoService servicoService, ServicoItemRepository servicoItemRepository,
                            OrcamentoServicoRepository orcamentoServicoRepository,
                            OrdemServicoService ordemServicoService, UsuarioRepository usuarioRepository){
        this.orcamentoRepository = repository;
        this.veiculoService = veiculoService;
        this.clienteService = clienteService;
        this.servicoService = servicoService;
        this.ordemServicoService = ordemServicoService;
        this.servicoItemRepository = servicoItemRepository;
        this.orcamentoServicoRepository = orcamentoServicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void gerarOrcamento(GeracaoOrcamentoRequestDTO dto,String usuarioLogado) {
        Orcamento orcamento = new Orcamento();
        Cliente cliente = clienteService.pesquisarPorId(dto.clienteId());
        Veiculo veiculo = veiculoService.pesquisarPorId(dto.veiculoId());
        Usuario usuario = usuarioRepository.findByLogin(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        orcamento.setCliente(cliente);
        orcamento.setVeiculo(veiculo);
        orcamento.setDataCriacao(LocalDateTime.now());
        orcamento.setStatusOrcamento(StatusOrcamento.PENDENTE);
        orcamento.setValorTotal(BigDecimal.ZERO); // valor provisório, ajustado depois
        orcamento = orcamentoRepository.save(orcamento);

        ordemServicoService.gerarOrdemServico(orcamento.getId(), usuario);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (Integer id : dto.servicosIds()) {
            Servico servico = servicoService.pesquisarPorId(id);
            BigDecimal valorMaoDeObra = servico.getValorMaoDeObra();
            BigDecimal valorItems = BigDecimal.ZERO;

            List<ServicoItem> itensDoServico = servicoItemRepository.findByServico_Id(servico.getId());
            for (ServicoItem servicoItem : itensDoServico) {
                BigDecimal valorItem = servicoItem.getItemEstoque().getValorUnitario()
                        .multiply(BigDecimal.valueOf(servicoItem.getQuantidadePadrao()));
                valorItems = valorItems.add(valorItem);
            }

            BigDecimal valorServicoCompleto = valorMaoDeObra.add(valorItems);
            valorTotal = valorTotal.add(valorServicoCompleto);


            orcamentoServicoRepository.save(new OrcamentoServico(orcamento, servico, valorServicoCompleto));

        }



        orcamento.setValorTotal(valorTotal);
        orcamentoRepository.save(orcamento);
    }

    public Orcamento pesquisarPorId(Integer id){
        return orcamentoRepository.findById(id).orElseThrow(() -> new OrcamentoNaoEncontradoException("Orçamento nao encontrado"));
    }
}
