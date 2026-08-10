package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.dto.insumo.AtualizacaoInsumoRequestDTO;
import com.kap.mechanics_api.dto.insumo.CriacaoInsumoRequestDTO;
import com.kap.mechanics_api.dto.insumo.InsumoResponseDTO;
import com.kap.mechanics_api.service.InsumoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {
    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) { this.insumoService = insumoService; }

    @PostMapping
    public ResponseEntity<InsumoResponseDTO> cadastrar(@Valid @RequestBody CriacaoInsumoRequestDTO dto) {
        InsumoResponseDTO response = insumoService.cadastrar(dto);
        return ResponseEntity.created(URI.create("/api/insumos/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InsumoResponseDTO>> listar() { return ResponseEntity.ok(insumoService.listar()); }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(insumoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> atualizar(@PathVariable Integer id,
                                                        @Valid @RequestBody AtualizacaoInsumoRequestDTO dto) {
        return ResponseEntity.ok(insumoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        insumoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
