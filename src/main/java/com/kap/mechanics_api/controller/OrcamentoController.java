package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.dto.orcamento.AtualizacaoStatusOrcamentoRequestDTO;
import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
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

@RestController
@RequestMapping("/api/orcamento")
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService orcamentoService) {
        this.orcamentoService = orcamentoService;
    }

    @PostMapping("/gerarOrcamento")
    public ResponseEntity<String> gerarOrcamento(
            @Valid @RequestBody GeracaoOrcamentoRequestDTO dto,
            Authentication authentication) {
        orcamentoService.gerarOrcamento(dto, authentication.getName());
        return ResponseEntity.ok("Orçamento gerado com sucesso.");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> responderOrcamento(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizacaoStatusOrcamentoRequestDTO dto) {
        orcamentoService.responder(id, dto.status());
        return ResponseEntity.noContent().build();
    }
}
