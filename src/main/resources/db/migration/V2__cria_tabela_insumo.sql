CREATE TABLE insumo (
    id                  BIGSERIAL PRIMARY KEY,
    nome                VARCHAR(150) NOT NULL,
    descricao           TEXT NOT NULL,
    valor_unitario      DECIMAL(10,2) NOT NULL CHECK (valor_unitario >= 0),
    quantidade_atual    INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_atual >= 0),
    quantidade_minima   INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_minima >= 0),
    ativo               BOOLEAN NOT NULL DEFAULT TRUE
);
