package com.kap.mechanics_api.usuario;

import com.kap.mechanics_api.adapter.security.CriptografiaSenhaAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

class CriptografiaSenhaAdapterTest {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final CriptografiaSenhaAdapter adapter = new CriptografiaSenhaAdapter(encoder);

    @Test
    void deveGerarHashCompativelComAutenticacao() {
        String hash = adapter.codificar("segredo");

        assertNotEquals("segredo", hash);
        assertTrue(encoder.matches("segredo", hash));
        assertFalse(encoder.matches("outra-senha", hash));
    }

    @Test
    void deveGerarHashesDistintosParaMesmaSenha() {
        String primeiro = adapter.codificar("segredo");
        String segundo = adapter.codificar("segredo");

        assertNotEquals(primeiro, segundo);
        assertTrue(encoder.matches("segredo", primeiro));
        assertTrue(encoder.matches("segredo", segundo));
    }
}
