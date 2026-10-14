package com.kap.mechanics_api.core.veiculo.gateway;

import com.kap.mechanics_api.core.veiculo.domain.Veiculo;

import java.util.List;
import java.util.Optional;

public interface VeiculoGateway {

    Veiculo salvar(Veiculo veiculo);
    List<Veiculo> listarTodos();
    Optional<Veiculo> pesquisarPorId(Integer id);
    void excluir(Integer id);
    // Registra o cliente como proprietário do veículo; não duplica um vínculo já existente.
    void vincularCliente(Integer veiculoId, Integer clienteId);

}
