package com.kap.mechanics_api.documentation;

import com.kap.mechanics_api.dto.ordemservico.AtualizacaoStatusOrdemServicoRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrdemServicoControllerDoc {

    @Operation(summary = "Gerar uma ordem de serviço a partir de um orçamento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ordem de serviço gerada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Orçamento ou usuário não encontrado")
    })
    ResponseEntity<Void> gerarOrdemServico(
            @PathVariable Integer orcamentoId,
            Authentication authentication
    );

    @Operation(summary = "Atualizar o status de uma ordem de serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status da ordem de serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status ou transição inválida"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    ResponseEntity<Void> transicionarStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizacaoStatusOrdemServicoRequestDTO dto
    );
}
