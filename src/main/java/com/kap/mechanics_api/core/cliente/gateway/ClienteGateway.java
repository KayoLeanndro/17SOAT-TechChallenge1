package com.kap.mechanics_api.core.cliente.gateway;

import com.kap.mechanics_api.core.cliente.domain.Cliente;
import com.kap.mechanics_api.core.cliente.domain.CpfCnpj;

import java.util.List;
import java.util.Optional;

public interface ClienteGateway {

    Cliente salvar(Cliente cliente);
    List<Cliente> listarTodos();
    Optional<Cliente> pesquisarPorId(Integer id);
    Optional<Cliente> buscarPorDocumento(CpfCnpj cpfCnpj);
    void excluir(Integer id);

}
