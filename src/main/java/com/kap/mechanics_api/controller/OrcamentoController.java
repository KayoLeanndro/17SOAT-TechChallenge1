package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.service.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orcamento")
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService service){
        this.orcamentoService = service;
    }

    @PostMapping("/gerarOrcamento")
    public ResponseEntity<String> gerarOrcamento(@Valid @RequestBody GeracaoOrcamentoRequestDTO dto){

        orcamentoService.gerarOrcamento(dto);
        return ResponseEntity.ok("orçamento gerado com sucesso!");
    }

}
