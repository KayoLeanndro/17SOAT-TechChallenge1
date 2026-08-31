CREATE TABLE orcamento_item (
    id BIGSERIAL PRIMARY KEY,
    orcamento_id BIGINT NOT NULL REFERENCES orcamento(id),
    servico_id BIGINT REFERENCES servico(id),
    item_estoque_id BIGINT REFERENCES item_estoque(id),
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    valor_unitario_cobrado DECIMAL(10,2) NOT NULL CHECK (valor_unitario_cobrado >= 0),
    CONSTRAINT ck_orcamento_item_referencia CHECK (
        (servico_id IS NOT NULL AND item_estoque_id IS NULL) OR
        (servico_id IS NULL AND item_estoque_id IS NOT NULL)
    )
);

CREATE TABLE ordem_servico_item (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordem_servico(id),
    orcamento_item_id BIGINT REFERENCES orcamento_item(id),
    servico_id BIGINT REFERENCES servico(id),
    item_estoque_id BIGINT REFERENCES item_estoque(id),
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    valor_unitario_cobrado DECIMAL(10,2) NOT NULL CHECK (valor_unitario_cobrado >= 0),
    CONSTRAINT ck_ordem_servico_item_referencia CHECK (
        (servico_id IS NOT NULL AND item_estoque_id IS NULL) OR
        (servico_id IS NULL AND item_estoque_id IS NOT NULL)
    )
);

CREATE INDEX idx_orcamento_item_orcamento ON orcamento_item(orcamento_id);
CREATE INDEX idx_ordem_servico_item_os ON ordem_servico_item(ordem_servico_id);
