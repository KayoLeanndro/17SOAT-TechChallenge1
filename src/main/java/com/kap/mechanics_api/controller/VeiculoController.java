package com.kap.mechanics_api.controller;

import com.kap.mechanics_api.documentation.VeiculoControllerDoc;
import com.kap.mechanics_api.dto.veiculo.*;
import com.kap.mechanics_api.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController()
@RequestMapping("/api/veiculo")
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public class VeiculoController implements VeiculoControllerDoc {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService service){
        this.veiculoService= service;
    }


    @PostMapping
    @Override
    public ResponseEntity<CriacaoVeiculoResponseDTO> cadastrar(@RequestBody @Valid CriacaoVeiculoRequestDTO dto){

        CriacaoVeiculoResponseDTO response = veiculoService.cadastrar(dto);
        URI location = URI.create("/api/veiculo/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ListagemVeiculoResponseDTO>> listar(){
        return ResponseEntity.ok(veiculoService.listar());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ListagemVeiculoResponseDTO> pesquisarPorId(@PathVariable Integer id){
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<AtualizacaoVeiculoResponseDTO> atualizar(@RequestBody AtualizacaoVeiculoRequestDTO dto, @PathVariable Integer id){
        return ResponseEntity.ok(veiculoService.atualizar(dto, id));
    }

}
