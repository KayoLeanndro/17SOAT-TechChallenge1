package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.documentation.OrcamentoControllerDoc;
import com.kap.mechanics_api.dto.orcamento.AtualizacaoStatusOrcamentoRequestDTO;
import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.dto.orcamento.InclusaoOrcamentoItemRequestDTO;
import com.kap.mechanics_api.enums.StatusOrcamento;
import com.kap.mechanics_api.service.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kap.mechanics_api.dto.orcamento.ConsultaOrcamentoItensResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/orcamento")
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public class OrcamentoController implements OrcamentoControllerDoc {

    private final OrcamentoService orcamentoService;
    private final com.kap.mechanics_api.service.OrcamentoItemService orcamentoItemService;

    public OrcamentoController(OrcamentoService orcamentoService, com.kap.mechanics_api.service.OrcamentoItemService orcamentoItemService) {
        this.orcamentoService = orcamentoService;
        this.orcamentoItemService = orcamentoItemService;
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<Void> incluirItem(@PathVariable Integer id, @Valid @RequestBody InclusaoOrcamentoItemRequestDTO dto) {
        orcamentoItemService.incluir(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/itens")
    public ResponseEntity<ConsultaOrcamentoItensResponseDTO> consultarItens(@PathVariable Integer id) {
        return ResponseEntity.ok(orcamentoItemService.consultar(id));
    }

    @PostMapping("/gerarOrcamento")
    @Override
    public ResponseEntity<String> gerarOrcamento(
            @Valid @RequestBody GeracaoOrcamentoRequestDTO dto,
            Authentication authentication) {
        orcamentoService.gerarOrcamento(dto, authentication.getName());
        return ResponseEntity.ok("Orçamento gerado com sucesso.");
    }

    @PatchMapping("/{id}/status")
    @Override
    public ResponseEntity<String> atualizarStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizacaoStatusOrcamentoRequestDTO dto) {

        orcamentoService.atualizarStatus(id, dto.status());
        return ResponseEntity.ok("status do orçamento atualizado com sucesso!");
    }

    public ResponseEntity<Void> responderOrcamento(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizacaoStatusOrcamentoRequestDTO dto) {

        StatusOrcamento status = StatusOrcamento.valueOf(dto.status());
        orcamentoService.responder(id, status);
        return ResponseEntity.noContent().build();
    }
}
