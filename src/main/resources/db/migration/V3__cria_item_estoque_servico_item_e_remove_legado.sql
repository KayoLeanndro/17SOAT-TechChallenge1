CREATE TABLE item_estoque (
    id BIGSERIAL PRIMARY KEY,
    item_estoque_nome VARCHAR(150) NOT NULL,
    item_estoque_descricao TEXT NOT NULL,
    tipo_item_estoque VARCHAR(20) NOT NULL CHECK (tipo_item_estoque IN ('PECA', 'INSUMO')),
    item_estoque_valor DECIMAL(10,2) NOT NULL CHECK (item_estoque_valor >= 0),
    quantidade_atual INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_atual >= 0),
    quantidade_minima INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_minima >= 0),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE servico_item (
    servico_id BIGINT NOT NULL REFERENCES servico(id),
    item_estoque_id BIGINT NOT NULL REFERENCES item_estoque(id),
    quantidade_padrao INTEGER NOT NULL CHECK (quantidade_padrao > 0),
    PRIMARY KEY (servico_id, item_estoque_id)
);

INSERT INTO item_estoque (
    item_estoque_nome,
    item_estoque_descricao,
    tipo_item_estoque,
    item_estoque_valor,
    quantidade_atual,
    quantidade_minima,
    ativo
)
VALUES
('Filtro de óleo', 'Filtro de óleo para troca preventiva', 'PECA', 35.90, 50, 10, TRUE),
('Óleo 5W30', 'Óleo sintético 5W30 para motor', 'INSUMO', 42.50, 120, 20, TRUE),
('Pastilha de freio dianteira', 'Jogo de pastilhas dianteiras', 'PECA', 189.90, 18, 4, TRUE),
('Limpa contato', 'Spray limpa contato para componentes elétricos', 'INSUMO', 18.75, 80, 15, TRUE),
('Filtro de ar', 'Filtro de ar para motor', 'PECA', 47.00, 35, 8, TRUE),
('Desengraxante', 'Desengraxante multiuso para oficina', 'INSUMO', 29.90, 60, 12, TRUE);

INSERT INTO servico (
    nome,
    descricao,
    valor_mao_obra,
    tempo_estimado_min,
    ativo
)
VALUES
('Troca de óleo', 'Substituição completa do óleo do motor e filtro', 120.00, 40, TRUE),
('Revisão de freios', 'Inspeção e troca de componentes do sistema de freio', 220.00, 90, TRUE),
('Revisão elétrica', 'Limpeza e verificação de conexões elétricas', 180.00, 75, TRUE),
('Troca de filtro de ar', 'Substituição do filtro de ar do motor', 80.00, 25, TRUE);

INSERT INTO servico_item (quantidade_padrao, item_estoque_id, servico_id)
SELECT 1, i.id, s.id
FROM item_estoque i
JOIN servico s ON s.nome = 'Troca de óleo'
WHERE i.item_estoque_nome = 'Filtro de óleo';

INSERT INTO servico_item (quantidade_padrao, item_estoque_id, servico_id)
SELECT 4, i.id, s.id
FROM item_estoque i
JOIN servico s ON s.nome = 'Troca de óleo'
WHERE i.item_estoque_nome = 'Óleo 5W30';

INSERT INTO servico_item (quantidade_padrao, item_estoque_id, servico_id)
SELECT 1, i.id, s.id
FROM item_estoque i
JOIN servico s ON s.nome = 'Revisão de freios'
WHERE i.item_estoque_nome = 'Pastilha de freio dianteira';

INSERT INTO servico_item (quantidade_padrao, item_estoque_id, servico_id)
SELECT 1, i.id, s.id
FROM item_estoque i
JOIN servico s ON s.nome = 'Revisão elétrica'
WHERE i.item_estoque_nome = 'Limpa contato';

INSERT INTO servico_item (quantidade_padrao, item_estoque_id, servico_id)
SELECT 1, i.id, s.id
FROM item_estoque i
JOIN servico s ON s.nome = 'Troca de filtro de ar'
WHERE i.item_estoque_nome = 'Filtro de ar';

ALTER TABLE movimentacao_estoque RENAME COLUMN peca_id TO item_estoque_id;
ALTER INDEX idx_movimentacao_estoque_peca RENAME TO idx_movimentacao_estoque_item_estoque;

DROP TABLE IF EXISTS os_peca CASCADE;
DROP TABLE IF EXISTS orcamento_peca CASCADE;
DROP TABLE IF EXISTS servico_peca CASCADE;
DROP TABLE IF EXISTS insumo CASCADE;
DROP TABLE IF EXISTS peca CASCADE;

ALTER TABLE movimentacao_estoque
    ADD CONSTRAINT fk_movimentacao_estoque_item_estoque
        FOREIGN KEY (item_estoque_id) REFERENCES item_estoque(id);
