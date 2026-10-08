package com.kap.mechanics_api.core.cliente.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CpfCnpjTest {

    @Test
    void deveRejeitarDocumentoVazio(){

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CpfCnpj("")
        );

        assertEquals("O CPF ou CNPJ deve ser informado", ex.getMessage());
    }

    @Test
    void deveRejeitarCpfCnpjComMaisDe14digitos(){

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CpfCnpj("123456789101111")
        );

        assertEquals("O documento deve possuir 11 dígitos para CPF ou 14 para CNPJ", ex.getMessage());

    }

    @Test
    void deveRejeitarQuandoPossuirCaracteresInvalidos(){
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CpfCnpj("@@@@@!!!!!@@@@")
        );

        assertEquals("O documento contém caracteres inválidos", ex.getMessage());
    }

    @Test
    void deveCriarCpfComSucesso(){

        String cpfDigitado = "12965534423";

        CpfCnpj cpf = new CpfCnpj(cpfDigitado);

        assertEquals(cpfDigitado, cpf.getDocumento());
    }

    @Test
    void deveCriarCnpjComSucesso(){

        String cnpjDigitado = "76803621000175";

        CpfCnpj cnpj = new CpfCnpj(cnpjDigitado);

        assertEquals(cnpjDigitado, cnpj.getDocumento());
    }

    @Test
    void deveCompararValoresCPFIguais(){
        CpfCnpj formatado = new CpfCnpj("123.456.789-09");
        CpfCnpj semFormato = new CpfCnpj("12345678909");

        assertEquals(formatado, semFormato);
        assertEquals(formatado.hashCode(), semFormato.hashCode());
    }

    @Test
    void deveDiferenciarDocumentosDistintos(){
        CpfCnpj cpf = new CpfCnpj("12345678909");
        CpfCnpj outroCpf = new CpfCnpj("98765432100");

        assertNotEquals(cpf, outroCpf);
    }


    @Test
    void deveLimparDigitosCpfCnpj(){
        CpfCnpj cpfFormatado = new CpfCnpj("123.456.789-09");
        CpfCnpj cnpjFormatado = new CpfCnpj("76.803.621/0001-75");

        assertEquals("12345678909", cpfFormatado.getDocumento());
        assertEquals("76803621000175", cnpjFormatado.getDocumento());
    }

    @Test
    void deveRejeitarDocumentoNulo(){
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CpfCnpj(null)
        );

        assertEquals("O CPF ou CNPJ deve ser informado", ex.getMessage());
    }

    @Test
    void deveRejeitarDocumentoApenasComEspacos(){
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CpfCnpj("           ")
        );

        assertEquals("O CPF ou CNPJ deve ser informado", ex.getMessage());
    }

    @Test
    void deveRejeitarDocumentoComLetras(){

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CpfCnpj("123abc456def78900")
        );

        assertEquals("O documento contém caracteres inválidos", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890", "123456789012", "123456789101111"})
    void deveRejeitarQuantidadeDeDigitosInvalida(String documento) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new CpfCnpj(documento));
        assertEquals("O documento deve possuir 11 dígitos para CPF ou 14 para CNPJ", ex.getMessage());
    }


}
