package com.kap.mechanics_api.adapter.security;

import com.kap.mechanics_api.core.usuario.gateway.CriptografiaSenhaGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CriptografiaSenhaAdapter implements CriptografiaSenhaGateway {

    private final PasswordEncoder passwordEncoder;

    public CriptografiaSenhaAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String codificar(String senha) {
        return passwordEncoder.encode(senha);
    }
}