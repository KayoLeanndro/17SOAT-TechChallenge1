package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.documentation.ItemEstoqueControllerDoc;
import com.kap.mechanics_api.dto.itemestoque.AtualizacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.CriacaoItemEstoqueRequestDTO;
import com.kap.mechanics_api.dto.itemestoque.ItemEstoqueResponseDTO;
import com.kap.mechanics_api.service.ItemEstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/item-estoque")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class ItemEstoqueController implements ItemEstoqueControllerDoc {

    private final ItemEstoqueService itemEstoqueService;

    public ItemEstoqueController(ItemEstoqueService itemEstoqueService) {
        this.itemEstoqueService = itemEstoqueService;
    }

    @PostMapping
    @Override
    public ResponseEntity<ItemEstoqueResponseDTO> cadastrar(@Valid @RequestBody CriacaoItemEstoqueRequestDTO dto) {
        ItemEstoqueResponseDTO response = itemEstoqueService.cadastrar(dto);
        return ResponseEntity.created(URI.create("/api/item-estoque/" + response.id())).body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ItemEstoqueResponseDTO>> listar() {
        return ResponseEntity.ok(itemEstoqueService.listar());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ItemEstoqueResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(itemEstoqueService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<ItemEstoqueResponseDTO> atualizar(@PathVariable Integer id,
                                                            @Valid @RequestBody AtualizacaoItemEstoqueRequestDTO dto) {
        return ResponseEntity.ok(itemEstoqueService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        itemEstoqueService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
