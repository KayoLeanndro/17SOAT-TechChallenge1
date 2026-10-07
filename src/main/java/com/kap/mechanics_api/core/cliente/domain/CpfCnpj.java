package com.kap.mechanics_api.core.cliente.domain;

import java.util.Objects;

public final class CpfCnpj {

    private final String documento;

    public CpfCnpj(String digitoFormatado){

        if (digitoFormatado == null || digitoFormatado.isBlank()) {
            throw new IllegalArgumentException("O CPF ou CNPJ deve ser informado");
        }

        String digitos = digitoFormatado.trim().replaceAll("\\D", "");

        if (!digitoFormatado.matches("[\\d.\\-/\\s]+")) {
            throw new IllegalArgumentException("O documento contém caracteres inválidos");
        }

        if (digitos.length() != 11 && digitos.length() != 14) {
            throw new IllegalArgumentException(
                    "O documento deve possuir 11 dígitos para CPF ou 14 para CNPJ"
            );
        }
        this.documento = digitos;
    }

    public String getDocumento() {
        return documento;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CpfCnpj outro = (CpfCnpj) obj;
        return documento.equals(outro.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }
}
