package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.dto.orcamento.GeracaoOrcamentoRequestDTO;
import com.kap.mechanics_api.service.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orcamento")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService service){
        this.orcamentoService = service;
    }

    @PostMapping("/gerarOrcamento")
    public ResponseEntity<String> gerarOrcamento(@Valid @RequestBody GeracaoOrcamentoRequestDTO dto, Authentication authentication){

        orcamentoService.gerarOrcamento(dto, authentication.getName());
        return ResponseEntity.ok("orçamento gerado com sucesso!");
    }

}
