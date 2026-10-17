package com.kap.mechanics_api.service;

import com.kap.mechanics_api.adapter.persistence.entity.ItemEstoqueJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.ServicoItemJpaEntity;
import com.kap.mechanics_api.adapter.persistence.entity.ServicoJpaEntity;

import com.kap.mechanics_api.core.movimentacaoestoque.usecase.BaixarItemParaOrdemServicoUseCase;
import com.kap.mechanics_api.domain.*;
import com.kap.mechanics_api.dto.ordemservico.InclusaoOrdemServicoItemRequestDTO;
import com.kap.mechanics_api.enums.StatusOrdemServicoEnum;
import com.kap.mechanics_api.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import com.kap.mechanics_api.dto.ordemservico.ConsultaOrdemServicoItensResponseDTO;
import com.kap.mechanics_api.dto.ordemservico.ItemOrdemServicoResponseDTO;

@Service
public class OrdemServicoItemService {
	private final OrdemServicoRepository ordemServicoRepository;
	private final OrdemServicoItemRepository itemRepository;
	private final OrcamentoItemRepository orcamentoItemRepository;
	private final ServicoService servicoService;
	private final ItemEstoqueService itemEstoqueService;
	private final ServicoItemRepository servicoItemRepository;
	private final BaixarItemParaOrdemServicoUseCase baixarItemParaOrdemServicoUseCase;

	public OrdemServicoItemService(OrdemServicoRepository ordemServicoRepository,
			OrdemServicoItemRepository itemRepository, OrcamentoItemRepository orcamentoItemRepository,
			ServicoService servicoService, ItemEstoqueService itemEstoqueService,
			ServicoItemRepository servicoItemRepository, BaixarItemParaOrdemServicoUseCase baixarItemParaOrdemServicoUseCase) {
		this.ordemServicoRepository = ordemServicoRepository;
		this.itemRepository = itemRepository;
		this.orcamentoItemRepository = orcamentoItemRepository;
		this.servicoService = servicoService;
		this.itemEstoqueService = itemEstoqueService;
		this.servicoItemRepository = servicoItemRepository;
		this.baixarItemParaOrdemServicoUseCase = baixarItemParaOrdemServicoUseCase;
	}

	@Transactional
	public void copiarDoOrcamento(OrdemServico os) {
		if (!itemRepository.findByOrdemServico_Id(os.getId()).isEmpty())
			return;
		for (OrcamentoItem origem : orcamentoItemRepository.findByOrcamento_Id(os.getOrcamento().getId()))
			salvar(os, origem, origem.getServico(), origem.getItemEstoque(), origem.getQuantidade(),
					origem.getValorUnitarioCobrado());
	}

	@Transactional
	public void incluir(Integer ordemServicoId, InclusaoOrdemServicoItemRequestDTO dto) {
		OrdemServico os = ordemServicoRepository.findById(ordemServicoId).orElseThrow(
				() -> new com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEncontradaException(ordemServicoId));
		if (!StatusOrdemServicoEnum.EM_EXECUCAO.name().equals(os.getStatusOrdemServico().getNome()))
			throw new com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEstaEmExecucaoException(ordemServicoId);
		if ((dto.servicoId() == null) == (dto.itemEstoqueId() == null))
			throw new IllegalArgumentException("Informe exatamente um: servicoId ou itemEstoqueId.");
		if (dto.itemEstoqueId() != null)
			incluirEstoque(os, itemEstoqueService.pesquisarPorId(dto.itemEstoqueId()), dto.quantidade());
		else {
			ServicoJpaEntity servico = servicoService.pesquisarPorId(dto.servicoId());
			salvar(os, null, servico, null, dto.quantidade(), servico.getValorMaoDeObra());
			for (ServicoItemJpaEntity componente : servicoItemRepository.findByServico_Id(servico.getId()))
				incluirEstoque(os, componente.getItemEstoque(),
						Math.multiplyExact(dto.quantidade(), componente.getQuantidadePadrao()));
		}
	}

	@Transactional
	public ConsultaOrdemServicoItensResponseDTO consultar(Integer ordemServicoId) {
		OrdemServico os = ordemServicoRepository.findById(ordemServicoId).orElseThrow(
				() -> new com.kap.mechanics_api.core.ordemservico.usecase.OrdemServicoNaoEncontradaException(ordemServicoId));
		List<ItemOrdemServicoResponseDTO> itens = itemRepository.findByOrdemServico_Id(ordemServicoId).stream()
				.map(item -> new ItemOrdemServicoResponseDTO(item.getId(),
						item.getOrcamentoItem() == null ? null : item.getOrcamentoItem().getId(),
						item.getServico() == null ? "ESTOQUE" : "SERVICO",
						item.getServico() == null ? item.getItemEstoque().getId() : item.getServico().getId(),
						item.getServico() == null ? item.getItemEstoque().getNome() : item.getServico().getNome(),
						item.getQuantidade(), item.getValorUnitarioCobrado(),
						item.getValorUnitarioCobrado().multiply(BigDecimal.valueOf(item.getQuantidade()))))
				.toList();
		BigDecimal total = itens.stream().map(ItemOrdemServicoResponseDTO::valorTotal).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		return new ConsultaOrdemServicoItensResponseDTO(ordemServicoId, os.getOrcamento().getId(),
				os.getStatusOrdemServico().getNome(), total, itens);
	}

	@Transactional
	public void baixarItensEstoque(OrdemServico os) {
		for (OrdemServicoItem item : itemRepository.findByOrdemServico_Id(os.getId()))
			if (item.getItemEstoque() != null)
				baixarItemParaOrdemServicoUseCase.executar(os.getId(), item.getItemEstoque().getId(),
						item.getQuantidade());
	}

	private void incluirEstoque(OrdemServico os, ItemEstoqueJpaEntity estoque, int quantidade) {
		salvar(os, null, null, estoque, quantidade, estoque.getValorUnitario());
		baixarItemParaOrdemServicoUseCase.executar(os.getId(), estoque.getId(), quantidade);
	}

	private void salvar(OrdemServico os, OrcamentoItem origem, ServicoJpaEntity servico, ItemEstoqueJpaEntity estoque, int quantidade,
			BigDecimal valor) {
		OrdemServicoItem linha = new OrdemServicoItem();
		linha.setOrdemServico(os);
		linha.setOrcamentoItem(origem);
		linha.setServico(servico);
		linha.setItemEstoque(estoque);
		linha.setQuantidade(quantidade);
		linha.setValorUnitarioCobrado(valor);
		itemRepository.save(linha);
	}
}
