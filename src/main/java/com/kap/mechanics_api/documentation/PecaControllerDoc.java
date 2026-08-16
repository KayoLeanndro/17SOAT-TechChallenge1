package com.kap.mechanics_api.documentation;

import com.kap.mechanics_api.dto.peca.AtualizacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.CriacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.PecaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface PecaControllerDoc {

    @Operation(summary = "Cadastrar uma nova Peça")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Peça cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<PecaResponseDTO> cadastrar(@Valid @RequestBody CriacaoPecaRequestDTO dto);


    @Operation(summary = "Listar todas as peças")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de Peças retornada com sucesso")
    })
    public ResponseEntity<List<PecaResponseDTO>> listar() ;

    @Operation(summary = "Buscar Peça por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Peça encontrado"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrado")
    })
    public ResponseEntity<PecaResponseDTO> buscarPorId(@PathVariable Integer id);


    @Operation(summary = "Atualizar uma Peça")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Peça atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nenhum campo informado"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrado")
    })
    public ResponseEntity<PecaResponseDTO> atualizar(@PathVariable Integer id,
                                                     @Valid @RequestBody AtualizacaoPecaRequestDTO dto);

    @Operation(summary = "Excluir uma Peça")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Peça removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Integer id);

}
