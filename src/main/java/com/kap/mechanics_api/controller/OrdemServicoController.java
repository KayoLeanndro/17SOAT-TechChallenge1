package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.dto.ordemservico.AtualizacaoOrdemServicoRequestDTO;
import com.kap.mechanics_api.dto.ordemservico.CriacaoOrdemServicoRequestDTO;
import com.kap.mechanics_api.dto.ordemservico.OrdemServicoResponseDTO;
import com.kap.mechanics_api.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ordem-servico")
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> cadastrar(
            @Valid @RequestBody CriacaoOrdemServicoRequestDTO dto) {
        OrdemServicoResponseDTO response = ordemServicoService.cadastrar(dto);
        return ResponseEntity.created(URI.create("/api/ordem-servico/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> listar() {
        return ResponseEntity.ok(ordemServicoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody AtualizacaoOrdemServicoRequestDTO dto) {
        return ResponseEntity.ok(ordemServicoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        ordemServicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
