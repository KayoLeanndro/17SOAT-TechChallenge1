package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.documentation.MovimentacaoEstoqueControllerDoc;
import com.kap.mechanics_api.dto.movimentacaoestoque.MovimentacaoEstoqueResponseDTO;
import com.kap.mechanics_api.dto.movimentacaoestoque.RegistroEntradaMovimentacaoEstoqueRequestDTO;
import com.kap.mechanics_api.dto.movimentacaoestoque.RegistroSaidaMovimentacaoEstoqueRequestDTO;
import com.kap.mechanics_api.enums.TipoMovimentacaoEstoque;
import com.kap.mechanics_api.service.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacao-estoque")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class MovimentacaoEstoqueController implements MovimentacaoEstoqueControllerDoc {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public MovimentacaoEstoqueController(MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @Override
    @PostMapping("/entrada")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> registrarEntrada(
            @Valid @RequestBody RegistroEntradaMovimentacaoEstoqueRequestDTO dto,
            Authentication authentication) {
        MovimentacaoEstoqueResponseDTO response = movimentacaoEstoqueService
                .registrarEntrada(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/movimentacao-estoque/" + response.id()))
                .body(response);
    }

    @Override
    @PostMapping("/saida")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> registrarSaida(
            @Valid @RequestBody RegistroSaidaMovimentacaoEstoqueRequestDTO dto,
            Authentication authentication) {
        MovimentacaoEstoqueResponseDTO response = movimentacaoEstoqueService
                .registrarSaida(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/movimentacao-estoque/" + response.id()))
                .body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> listar() {
        return ResponseEntity.ok(movimentacaoEstoqueService.listar());
    }

    @Override
    @GetMapping("/item/{itemEstoqueId}")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> listarPorItem(@PathVariable Integer itemEstoqueId) {
        return ResponseEntity.ok(movimentacaoEstoqueService.listarPorItem(itemEstoqueId));
    }

    @Override
    @GetMapping("/ordem-servico/{ordemServicoId}")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> listarPorOrdemServico(@PathVariable Integer ordemServicoId) {
        return ResponseEntity.ok(movimentacaoEstoqueService.listarPorOrdemServico(ordemServicoId));
    }

    @Override
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> listarPorTipo(@PathVariable TipoMovimentacaoEstoque tipo) {
        return ResponseEntity.ok(movimentacaoEstoqueService.listarPorTipo(tipo));
    }

    @Override
    @GetMapping("/periodo")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(movimentacaoEstoqueService.listarPorPeriodo(inicio, fim));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(movimentacaoEstoqueService.buscarPorId(id));
    }
}
