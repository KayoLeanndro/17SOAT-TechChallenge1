package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.documentation.PecaControllerDoc;
import com.kap.mechanics_api.dto.peca.AtualizacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.CriacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.PecaResponseDTO;
import com.kap.mechanics_api.service.PecaService;
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
@RequestMapping("/api/peca")
public class PecaController implements PecaControllerDoc {

    private final PecaService pecaService;

    public PecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    @PostMapping
    public ResponseEntity<PecaResponseDTO> cadastrar(@Valid @RequestBody CriacaoPecaRequestDTO dto) {
        PecaResponseDTO response = pecaService.cadastrar(dto);
        return ResponseEntity.created(URI.create("/pecas/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PecaResponseDTO>> listar() {
        return ResponseEntity.ok(pecaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pecaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> atualizar(@PathVariable Integer id,
                                                      @Valid @RequestBody AtualizacaoPecaRequestDTO dto) {
        return ResponseEntity.ok(pecaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        pecaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
