INSERT INTO usuario (nome, login, senha_hash, tipo, data_criacao)
SELECT 'Sistema de estoque', 'sistema-estoque',
       '$2a$10$6qbDSYeNaTOXM03CUIuMnODFnInUtdd99MjpEsvRchkrVo0.C7qRi',
       'ADMIN', CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE login = 'sistema-estoque'
);

INSERT INTO movimentacao_estoque (
    item_estoque_id, tipo, quantidade, data_hora, ordem_servico_id, usuario_id
)
SELECT item.id,
       CAST('ENTRADA' AS tipo_movimentacao),
       item.quantidade_atual,
       CURRENT_TIMESTAMP,
       NULL,
       usuario_sistema.id
FROM item_estoque item
CROSS JOIN (
    SELECT id FROM usuario WHERE login = 'sistema-estoque'
) usuario_sistema
WHERE item.quantidade_atual > 0
  AND NOT EXISTS (
      SELECT 1
      FROM movimentacao_estoque movimentacao
      WHERE movimentacao.item_estoque_id = item.id
  );
