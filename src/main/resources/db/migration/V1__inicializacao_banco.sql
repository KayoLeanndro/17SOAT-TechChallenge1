
CREATE TYPE  tipo_usuario AS ENUM ('ATENDENTE', 'ESTOQUISTA', 'ADMIN');
CREATE TYPE  status_aprovacao_orcamento AS ENUM ('PENDENTE', 'APROVADO', 'REJEITADO');
CREATE TYPE  tipo_movimentacao AS ENUM ('ENTRADA', 'SAIDA');
CREATE TYPE  origem_item_os AS ENUM ('ORCADO', 'ADICIONAL');

-- =====================================================================
-- CLIENTE / VEÍCULO
-- =====================================================================

CREATE TABLE  cliente (
                         id              BIGSERIAL PRIMARY KEY,
                         nome            VARCHAR(150) NOT NULL,
                         cpf_cnpj        VARCHAR(18) NOT NULL,
                         telefone        VARCHAR(20),
                         email           VARCHAR(150),
                         data_criacao       TIMESTAMP NOT NULL DEFAULT now(),
                         CONSTRAINT uq_cliente_cpf_cnpj UNIQUE (cpf_cnpj)
);

CREATE TABLE  veiculo (
                         id              BIGSERIAL PRIMARY KEY,
                         placa           VARCHAR(8) NOT NULL,
                         marca           VARCHAR(80) NOT NULL,
                         modelo          VARCHAR(80) NOT NULL,
                         ano             SMALLINT NOT NULL,
                         data_criacao       TIMESTAMP NOT NULL DEFAULT now(),
                         CONSTRAINT uq_veiculo_placa UNIQUE (placa),
                         CONSTRAINT ck_veiculo_ano CHECK (ano BETWEEN 1900 AND 2100)
);

-- Vínculo entre cliente e veículo (permite histórico / múltiplos condutores)
CREATE TABLE  cliente_veiculo (
                                 cliente_id      BIGINT NOT NULL REFERENCES cliente(id),
                                 veiculo_id      BIGINT NOT NULL REFERENCES veiculo(id),
                                 data_vinculo    DATE NOT NULL DEFAULT CURRENT_DATE,
                                 PRIMARY KEY (cliente_id, veiculo_id)
);

-- =====================================================================
-- USUÁRIO (funcionários da oficina)
-- =====================================================================

CREATE TABLE  usuario (
                         id              BIGSERIAL PRIMARY KEY,
                         nome            VARCHAR(150) NOT NULL,
                         login           VARCHAR(60) NOT NULL,
                         senha_hash      VARCHAR(255) NOT NULL,
                         tipo           tipo_usuario NOT NULL,
                         data_criacao       TIMESTAMP NOT NULL DEFAULT now(),
                         CONSTRAINT uq_usuario_login UNIQUE (login)
);

-- =====================================================================
-- CATÁLOGO: SERVIÇO / PEÇA
-- =====================================================================

CREATE TABLE  servico (
                         id                  BIGSERIAL PRIMARY KEY,
                         nome                VARCHAR(150) NOT NULL,
                         descricao           TEXT,
                         valor_mao_obra      DECIMAL(10,2) NOT NULL CHECK (valor_mao_obra >= 0),
                         tempo_estimado_min  INTEGER CHECK (tempo_estimado_min >= 0),
                         ativo               BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE  peca (
                      id                  BIGSERIAL PRIMARY KEY,
                      nome                VARCHAR(150) NOT NULL,
                      descricao           TEXT,
                      valor_unitario      DECIMAL(10,2) NOT NULL CHECK (valor_unitario >= 0),
                      quantidade_atual    INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_atual >= 0),
                      quantidade_minima   INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_minima >= 0),
                      ativo               BOOLEAN NOT NULL DEFAULT TRUE
);

-- Catálogo: peças padrão associadas a um serviço (usado na geração automática do orçamento)
CREATE TABLE  servico_peca (
                              servico_id          BIGINT NOT NULL REFERENCES servico(id),
                              peca_id             BIGINT NOT NULL REFERENCES peca(id),
                              quantidade_padrao   INTEGER NOT NULL CHECK (quantidade_padrao > 0),
                              PRIMARY KEY (servico_id, peca_id)
);

-- =====================================================================
-- ORÇAMENTO
-- =====================================================================

CREATE TABLE  orcamento (
                           id                  BIGSERIAL PRIMARY KEY,
                           cliente_id          BIGINT NOT NULL REFERENCES cliente(id),
                           veiculo_id          BIGINT NOT NULL REFERENCES veiculo(id),
                           valor_total          DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (valor_total >= 0),
                           status_aprovacao    status_aprovacao_orcamento NOT NULL DEFAULT 'PENDENTE',
                           data_criacao        TIMESTAMP NOT NULL DEFAULT now(),
                           data_resposta        TIMESTAMP
);

-- Serviços incluídos no orçamento (valor "congelado" no momento da geração)
CREATE TABLE  orcamento_servico (
                                   orcamento_id        BIGINT NOT NULL REFERENCES orcamento(id),
                                   servico_id          BIGINT NOT NULL REFERENCES servico(id),
                                   valor_cobrado        DECIMAL(10,2) NOT NULL CHECK (valor_cobrado >= 0),
                                   PRIMARY KEY (orcamento_id, servico_id)
);

-- Peças incluídas no orçamento (padrão dos serviços + itens extras adicionados manualmente), valor "congelado" no momento da geração
CREATE TABLE  orcamento_peca (
                                orcamento_id            BIGINT NOT NULL REFERENCES orcamento(id),
                                peca_id                 BIGINT NOT NULL REFERENCES peca(id),
                                quantidade              INTEGER NOT NULL CHECK (quantidade > 0),
                                valor_unitario_cobrado  DECIMAL(10,2) NOT NULL CHECK (valor_unitario_cobrado >= 0),
                                PRIMARY KEY (orcamento_id, peca_id)
);

-- =====================================================================
-- ORDEM DE SERVIÇO (O.S.)
-- =====================================================================

CREATE TABLE  status_ordem_servico (
                                      id      SERIAL PRIMARY KEY,
                                      nome    VARCHAR(30) NOT NULL UNIQUE
);

INSERT INTO  status_ordem_servico (nome) VALUES
                                            ('RECEBIDA'),
                                            ('EM_DIAGNOSTICO'),
                                            ('AGUARDANDO_APROVACAO'),
                                            ('EM_EXECUCAO'),
                                            ('FINALIZADA'),
                                            ('ENTREGUE');

CREATE TABLE  ordem_servico (
                               id                      BIGSERIAL PRIMARY KEY,
                               orcamento_id            BIGINT NOT NULL UNIQUE REFERENCES orcamento(id),
                               usuario_atendente_id    BIGINT NOT NULL REFERENCES usuario(id),
                               status_id               INTEGER NOT NULL REFERENCES status_ordem_servico(id),
                               data_abertura            TIMESTAMP NOT NULL DEFAULT now(),
                               data_entrega              TIMESTAMP
);

-- Histórico de transições de status (base para o cálculo de tempo médio)
CREATE TABLE  historico_status_os (
                                     id                  BIGSERIAL PRIMARY KEY,
                                     ordem_servico_id    BIGINT NOT NULL REFERENCES ordem_servico(id),
                                     status_id           INTEGER NOT NULL REFERENCES status_ordem_servico(id),
                                     data_hora_inicio    TIMESTAMP NOT NULL DEFAULT now(),
                                     data_hora_fim       TIMESTAMP,
                                     CONSTRAINT ck_historico_periodo CHECK (data_hora_fim IS NULL OR data_hora_fim >= data_hora_inicio)
);

-- Peças efetivamente usadas/consumidas na O.S. (espelha o orçamento epode receber itens adicionais durante a execução)
CREATE TABLE  os_peca (
                         id                      BIGSERIAL PRIMARY KEY,
                         ordem_servico_id        BIGINT NOT NULL REFERENCES ordem_servico(id),
                         peca_id                 BIGINT NOT NULL REFERENCES peca(id),
                         quantidade              INTEGER NOT NULL CHECK (quantidade > 0),
                         valor_unitario_cobrado  DECIMAL(10,2) NOT NULL CHECK (valor_unitario_cobrado >= 0),
                         origem                  origem_item_os NOT NULL DEFAULT 'ORCADO',
                         data_criacao               TIMESTAMP NOT NULL DEFAULT now()
);

-- =====================================================================
-- ESTOQUE
-- =====================================================================

-- Ledger de movimentações de estoque: toda entrada (reposição manual)  e toda saída (automática, disparada pelo consumo em uma O.S.)
CREATE TABLE  movimentacao_estoque (
                                      id                  BIGSERIAL PRIMARY KEY,
                                      peca_id              BIGINT NOT NULL REFERENCES peca(id),
                                      tipo                 tipo_movimentacao NOT NULL,
                                      quantidade           INTEGER NOT NULL CHECK (quantidade > 0),
                                      data_hora            TIMESTAMP NOT NULL DEFAULT now(),
                                      ordem_servico_id     BIGINT REFERENCES ordem_servico(id),
                                      usuario_id           BIGINT NOT NULL REFERENCES usuario(id),
                                      CONSTRAINT ck_saida_tem_os CHECK (
                                          (tipo = 'SAIDA' AND ordem_servico_id IS NOT NULL) OR
                                          (tipo = 'ENTRADA' AND ordem_servico_id IS NULL)
                                          )
);

CREATE INDEX  idx_ordem_servico_status ON ordem_servico(status_id);
CREATE INDEX  idx_ordem_servico_atendente ON ordem_servico(usuario_atendente_id);
CREATE INDEX  idx_historico_status_os_os ON historico_status_os(ordem_servico_id);
CREATE INDEX  idx_historico_status_os_status ON historico_status_os(status_id);
CREATE INDEX  idx_os_peca_os ON os_peca(ordem_servico_id);
CREATE INDEX  idx_os_peca_peca ON os_peca(peca_id);
CREATE INDEX  idx_movimentacao_estoque_peca ON movimentacao_estoque(peca_id);
CREATE INDEX  idx_movimentacao_estoque_os ON movimentacao_estoque(ordem_servico_id);
CREATE INDEX  idx_orcamento_cliente ON orcamento(cliente_id);
CREATE INDEX  idx_orcamento_veiculo ON orcamento(veiculo_id);
CREATE INDEX  idx_cliente_veiculo_veiculo ON cliente_veiculo(veiculo_id);