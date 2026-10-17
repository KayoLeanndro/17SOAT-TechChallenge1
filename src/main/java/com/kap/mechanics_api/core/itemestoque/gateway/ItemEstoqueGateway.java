package com.kap.mechanics_api.core.itemestoque.gateway;

import com.kap.mechanics_api.core.itemestoque.domain.ItemEstoque;

import java.util.List;
import java.util.Optional;

public interface ItemEstoqueGateway {

    ItemEstoque salvar(ItemEstoque itemEstoque);
    List<ItemEstoque> listarTodos();
    Optional<ItemEstoque> pesquisarPorId(Integer id);
    // Busca já bloqueando o registro para escrita, para que duas movimentações simultâneas
    // do mesmo item não calculem o saldo sobre o mesmo valor. Exige transação aberta.
    Optional<ItemEstoque> pesquisarPorIdParaAtualizacao(Integer id);
    void excluir(Integer id);

}
