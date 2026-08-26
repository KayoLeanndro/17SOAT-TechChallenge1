package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.documentation.OrdemServicoControllerDoc;
import com.kap.mechanics_api.dto.ordemservico.AtualizacaoStatusOrdemServicoRequestDTO;
import com.kap.mechanics_api.service.OrdemServicoService;
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
@RequestMapping("/api/ordem-servico")
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public class OrdemServicoController implements OrdemServicoControllerDoc {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @PostMapping("/{orcamentoId}")
    @Override
    public ResponseEntity<Void> gerarOrdemServico(
            @PathVariable Integer orcamentoId,
            Authentication authentication) {
        var ordemServico = ordemServicoService.gerarOrdemServico(orcamentoId, authentication.getName());
        return ResponseEntity.created(java.net.URI.create("/api/ordem-servico/" + ordemServico.getId())).build();
    }

    @PatchMapping("/{id}/status")
    @Override
    public ResponseEntity<Void> transicionarStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizacaoStatusOrdemServicoRequestDTO dto) {
        ordemServicoService.transicionarStatus(id, dto.status());
        return ResponseEntity.noContent().build();
    }
}
