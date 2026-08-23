package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.dto.ordemservico.AtualizacaoStatusOrdemServicoRequestDTO;
import com.kap.mechanics_api.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ordem-servico")
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> transicionarStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizacaoStatusOrdemServicoRequestDTO dto) {
        ordemServicoService.transicionarStatus(id, dto.status());
        return ResponseEntity.noContent().build();
    }
}
