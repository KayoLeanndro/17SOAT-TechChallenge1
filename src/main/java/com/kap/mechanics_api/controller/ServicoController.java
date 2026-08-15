package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.documentation.ServicoControllerDoc;
import com.kap.mechanics_api.dto.servico.*;
import com.kap.mechanics_api.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servico")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class ServicoController implements ServicoControllerDoc {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @Override
    @PostMapping
    public ResponseEntity<ServicoResponseDTO> cadastrar(
            @Valid @RequestBody CriacaoServicoRequestDTO dto) {

        ServicoResponseDTO response = servicoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> listar() {

        return ResponseEntity.ok(servicoService.listar());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(
            @Valid @RequestBody AtualizacaoServicoRequestDTO dto,
            @PathVariable Integer id) {

        return ResponseEntity.ok(servicoService.atualizar(dto, id));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Integer id) {

        servicoService.deletar(id);

        return ResponseEntity.noContent().build();
    }
}
