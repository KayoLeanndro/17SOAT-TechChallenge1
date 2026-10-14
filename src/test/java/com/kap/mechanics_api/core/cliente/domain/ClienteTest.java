package com.kap.mechanics_api.core.cliente.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class ClienteTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 7, 10, 30);
    private static final CpfCnpj CPF = new CpfCnpj("12345678909");

    private Cliente clienteExistente() {
        return Cliente.reconstruir(1, "Maria", CPF, "11999990000", "maria@email.com", AGORA);
    }

    @Test
    void deveCriarNovoClienteSemIdComDataInformada(){

        Cliente cliente = Cliente.novo("Maria", CPF, "11999990000", "maria@email.com", AGORA);

        assertNull(cliente.getId());
        assertEquals("Maria", cliente.getNome());
        assertEquals(CPF, cliente.getCpfCnpj());
        assertEquals("11999990000", cliente.getTelefone());
        assertEquals("maria@email.com", cliente.getEmail());
        assertEquals(AGORA, cliente.getDataCriacao());
    }

    @Test
    void deveCriarNovoClienteSemTelefoneEEmail(){

        Cliente cliente = Cliente.novo("Maria", CPF, null, null, AGORA);

        assertNull(cliente.getTelefone());
        assertNull(cliente.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRejeitarNovoClienteSemNome(String nome){

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Cliente.novo(nome, CPF, "11999990000", "maria@email.com", AGORA)
        );

        assertEquals("O nome é um campo obrigatório", ex.getMessage());
    }

    @Test
    void deveRejeitarNovoClienteSemCpfCnpj(){

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Cliente.novo("Maria", null, "11999990000", "maria@email.com", AGORA)
        );

        assertEquals("CPF/CNPJ é obrigatório", ex.getMessage());
    }

    @Test
    void deveReconstruirClienteMantendoIdEDataOriginal(){

        LocalDateTime dataOriginal = LocalDateTime.of(2024, 1, 15, 8, 0);

        Cliente cliente = Cliente.reconstruir(42, "Maria", CPF, "11999990000", "maria@email.com", dataOriginal);

        assertEquals(42, cliente.getId());
        assertEquals("Maria", cliente.getNome());
        assertEquals(CPF, cliente.getCpfCnpj());
        assertEquals("11999990000", cliente.getTelefone());
        assertEquals("maria@email.com", cliente.getEmail());
        assertEquals(dataOriginal, cliente.getDataCriacao());
    }

    @Test
    void deveRejeitarReconstruirClienteSemNome(){

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Cliente.reconstruir(1, " ", CPF, "11999990000", "maria@email.com", AGORA)
        );

        assertEquals("O nome é um campo obrigatório", ex.getMessage());
    }

    @Test
    void deveRejeitarReconstruirClienteSemCpfCnpj(){

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Cliente.reconstruir(1, "Maria", null, "11999990000", "maria@email.com", AGORA)
        );

        assertEquals("CPF/CNPJ é obrigatório", ex.getMessage());
    }

    @Test
    void deveAtualizarTodosOsCampos(){

        Cliente cliente = clienteExistente();
        CpfCnpj novoCnpj = new CpfCnpj("76803621000175");

        cliente.atualizar("Maria Souza", novoCnpj, "11888880000", "maria.souza@email.com");

        assertEquals("Maria Souza", cliente.getNome());
        assertEquals(novoCnpj, cliente.getCpfCnpj());
        assertEquals("11888880000", cliente.getTelefone());
        assertEquals("maria.souza@email.com", cliente.getEmail());
    }

    @Test
    void deveAtualizarApenasCamposInformados(){

        Cliente cliente = clienteExistente();

        cliente.atualizar(null, null, "11888880000", null);

        assertEquals("Maria", cliente.getNome());
        assertEquals(CPF, cliente.getCpfCnpj());
        assertEquals("11888880000", cliente.getTelefone());
        assertEquals("maria@email.com", cliente.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveIgnorarValoresEmBrancoAoAtualizar(String valor){

        Cliente cliente = clienteExistente();

        cliente.atualizar(valor, null, valor, valor);

        assertEquals("Maria", cliente.getNome());
        assertEquals(CPF, cliente.getCpfCnpj());
        assertEquals("11999990000", cliente.getTelefone());
        assertEquals("maria@email.com", cliente.getEmail());
    }

    @Test
    void naoDeveAlterarIdNemDataCriacaoAoAtualizar(){

        Cliente cliente = clienteExistente();

        cliente.atualizar("Maria Souza", new CpfCnpj("76803621000175"), "11888880000", "maria.souza@email.com");

        assertEquals(1, cliente.getId());
        assertEquals(AGORA, cliente.getDataCriacao());
    }
}
