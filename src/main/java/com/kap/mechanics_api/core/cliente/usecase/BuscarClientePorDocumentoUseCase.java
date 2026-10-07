package com.kap.mechanics_api.core.cliente.usecase;


import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;
import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;

public class BuscarClientePorDocumentoUseCase {

    private final ClienteGateway gateway;

    public BuscarClientePorDocumentoUseCase(ClienteGateway gateway){
        this.gateway= gateway;
    }

    public Cliente executar(CpfCnpj cpfCnpj){
        return gateway.buscarPorDocumento(cpfCnpj).orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj.getDocumento()));
    }

}
