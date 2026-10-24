package com.kap.mechanics_api.core.ordemservico.usecase;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;
import com.kap.mechanics_api.core.cliente.usecase.ClienteNaoEncontradoException;
import com.kap.mechanics_api.core.ordemservico.domain.OrdemServicoResumo;
import com.kap.mechanics_api.core.ordemservico.gateway.OrdemServicoGateway;

import java.util.List;

/**
 * Lista as ordens de um cliente, identificado pelo id ou pelo CPF/CNPJ (um, e só um, dos dois).
 */
public class ListarOrdensServicoPorClienteUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ClienteGateway clienteGateway;

    public ListarOrdensServicoPorClienteUseCase(OrdemServicoGateway ordemServicoGateway,
                                                ClienteGateway clienteGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.clienteGateway = clienteGateway;
    }

    public List<OrdemServicoResumo> executar(Integer clienteId, String cpfCnpj) {
        return ordemServicoGateway.listarResumosPorCliente(obterIdDoCliente(clienteId, cpfCnpj));
    }

    private Integer obterIdDoCliente(Integer clienteId, String cpfCnpj) {
        boolean temDocumento = cpfCnpj != null && !cpfCnpj.isBlank();

        if (clienteId != null && temDocumento) {
            throw new IllegalArgumentException("Informe apenas o ID do cliente ou CPF/CNPJ");
        }
        if (clienteId != null) {
            return clienteGateway.pesquisarPorId(clienteId)
                    .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId))
                    .getId();
        }
        if (!temDocumento) {
            throw new IllegalArgumentException("Informe o ID do cliente ou CPF/CNPJ");
        }

        CpfCnpj documento = new CpfCnpj(cpfCnpj);
        Cliente cliente = clienteGateway.buscarPorDocumento(documento)
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj));
        return cliente.getId();
    }
}
