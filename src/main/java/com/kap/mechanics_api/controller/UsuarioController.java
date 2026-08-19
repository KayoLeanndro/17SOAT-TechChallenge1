package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.documentation.UsuarioControllerDoc;
import com.kap.mechanics_api.dto.usuario.*;
import com.kap.mechanics_api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController implements UsuarioControllerDoc {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService service){
        this.usuarioService = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<CriacaoUsuarioResponseDTO> cadastrar(@Valid @RequestBody CriacaoUsuarioRequestDTO dto){
        CriacaoUsuarioResponseDTO response = usuarioService.cadastrar(dto);
        URI location = URI.create("/api/usuario/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ListagemUsuarioDTO> pesquisarPorId(@PathVariable Integer id){
        return ResponseEntity.ok(usuarioService.pesquisarPorId(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ListagemUsuarioDTO>> listar(){
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<AtualizacaoUsuarioResponseDTO> atualizar(@PathVariable Integer id, @RequestBody AtualizacaoUsuarioRequestDTO dto){
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
