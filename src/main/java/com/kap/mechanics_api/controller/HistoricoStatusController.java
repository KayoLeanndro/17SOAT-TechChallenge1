package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.dto.ordemservico.HistoricoStatusOsResponseDTO;
import com.kap.mechanics_api.service.HistoricoStatusOsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ordem-servico")
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public class HistoricoStatusController {

    private final HistoricoStatusOsService historicoStatusOsService;

    public HistoricoStatusController(HistoricoStatusOsService historicoStatusOsService) {
        this.historicoStatusOsService = historicoStatusOsService;
    }

    @GetMapping("/{ordemServicoId}/historico-status")
    @Operation(summary = "Consultar o histórico de status de uma ordem de serviço")
    public List<HistoricoStatusOsResponseDTO> buscarPorOrdemServico(
            @PathVariable Integer ordemServicoId) {
        return historicoStatusOsService.buscarPorOrdemServico(ordemServicoId);
    }
}
