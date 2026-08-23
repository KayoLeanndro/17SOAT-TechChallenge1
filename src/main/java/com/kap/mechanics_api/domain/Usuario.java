package com.kap.mechanics_api.domain;

/*
id              BIGSERIAL PRIMARY KEY,
nome            VARCHAR(150) NOT NULL,
login           VARCHAR(60) NOT NULL,
senha_hash      VARCHAR(255) NOT NULL,
tipo           tipo_usuario NOT NULL,
data_criacao       TIMESTAMP NOT NULL DEFAULT now(),
CONSTRAINT uq_usuario_login UNIQUE (login)
*/

import com.kap.mechanics_api.enums.TipoUsuario;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "usuario",
    uniqueConstraints = {@UniqueConstraint(name = "uq_usuario_login", columnNames = "login")}
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, name = "tipo")
    private TipoUsuario tipo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    public Usuario(){}

    public Usuario(Integer id,String nome, String login, String senhaHash, TipoUsuario tipo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senhaHash = senhaHash;
        this.tipo = tipo;
        this.dataCriacao = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
