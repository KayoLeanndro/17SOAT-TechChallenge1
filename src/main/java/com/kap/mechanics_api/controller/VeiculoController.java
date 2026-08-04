package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.dto.*;
import com.kap.mechanics_api.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController()
@RequestMapping("/api/veiculo")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService service){
        this.veiculoService= service;
    }


    @PostMapping
    public ResponseEntity<CriacaoVeiculoResponseDTO> cadastrar(@RequestBody @Valid CriacaoVeiculoRequestDTO dto){

        CriacaoVeiculoResponseDTO response = veiculoService.cadastrar(dto);
        URI location = URI.create("/api/veiculo/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ListagemVeiculoResponseDTO>> listar(){
        return ResponseEntity.ok(veiculoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListagemVeiculoResponseDTO> pesquisarPorId(@PathVariable Integer id){
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<AtualizacaoVeiculoResponseDTO> atualizar(@RequestBody AtualizacaoVeiculoRequestDTO dto, @PathVariable String id){
        return null;
    }

}
