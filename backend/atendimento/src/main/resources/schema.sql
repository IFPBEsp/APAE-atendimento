-- =============================================================================
-- SCHEMA: cadastro  (emulação do módulo de cadastro)
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS cadastro;

-- -----------------------------------------------------------------------------
-- TABELAS BASE
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS cadastro.enderecos (
    id          UUID         PRIMARY KEY,
    cidade      VARCHAR(255) NOT NULL,
    cep         VARCHAR(20)  NOT NULL,
    estado      VARCHAR(100) NOT NULL,
    bairro      VARCHAR(255) NOT NULL,
    rua         VARCHAR(255) NOT NULL,
    numero      VARCHAR(20)  NOT NULL,
    complemento VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS cadastro.areas_de_atendimento (
    id   SERIAL       PRIMARY KEY,
    area VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cadastro.pacientes (
    id                            UUID         PRIMARY KEY,
    nome_completo                 VARCHAR(255) NOT NULL,
    naturalidade                  VARCHAR(255) NOT NULL,
    data_de_nascimento            DATE         NOT NULL,
    contato                       VARCHAR(50)  NOT NULL,
    numero_registro_de_nascimento VARCHAR(100) NOT NULL,
    cartorio                      VARCHAR(255) NOT NULL,
    fls                           VARCHAR(50)  NOT NULL,
    livro                         VARCHAR(50)  NOT NULL,
    rg                            VARCHAR(50)  NOT NULL UNIQUE,
    data_de_emissao               DATE         NOT NULL,
    orgao_emissor                 VARCHAR(100) NOT NULL,
    cpf                           VARCHAR(14)  NOT NULL UNIQUE,
    cns                           VARCHAR(20)  NOT NULL,
    nis                           VARCHAR(20)  NOT NULL,
    data_de_cadastro              DATE         NOT NULL,
    alergias                      VARCHAR(500) NOT NULL,
    is_aluno                      BOOLEAN      NOT NULL DEFAULT FALSE,
    is_apagado                    BOOLEAN      NOT NULL DEFAULT FALSE,
    endereco_id                   UUID         REFERENCES cadastro.enderecos(id)
);

CREATE TABLE IF NOT EXISTS cadastro.responsaveis (
    id          UUID         PRIMARY KEY,
    nome        VARCHAR(255) NOT NULL,
    contato     VARCHAR(50)  NOT NULL,
    parentesco  VARCHAR(100) NOT NULL,
    endereco_id UUID         REFERENCES cadastro.enderecos(id),
    paciente_id UUID         NOT NULL REFERENCES cadastro.pacientes(id)
);

CREATE TABLE IF NOT EXISTS cadastro.parentes (
    id          UUID         PRIMARY KEY,
    nome        VARCHAR(255) NOT NULL,
    rg          VARCHAR(50)  NOT NULL,
    cpf         VARCHAR(14)  NOT NULL,
    vivo        BOOLEAN      NOT NULL,
    profissao   VARCHAR(255) NOT NULL,
    parentesco  VARCHAR(100) NOT NULL,
    paciente_id UUID         NOT NULL REFERENCES cadastro.pacientes(id)
);

CREATE TABLE IF NOT EXISTS cadastro.profissionais_da_saude (
    id                     UUID         PRIMARY KEY,
    nome                   VARCHAR(255) NOT NULL,
    email                  VARCHAR(255) NOT NULL UNIQUE,
    senha                  VARCHAR(255),
    perfil                 VARCHAR(100),
    area_de_atendimento    VARCHAR(255) REFERENCES cadastro.areas_de_atendimento(area) ON UPDATE CASCADE,
    contato                VARCHAR(50)  NOT NULL,
    documento_profissional VARCHAR(100) UNIQUE,
    rg                     VARCHAR(50)  UNIQUE,
    ativo                  BOOLEAN      NOT NULL DEFAULT TRUE,
    endereco_id            UUID         REFERENCES cadastro.enderecos(id),
    foto_perfil            VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS cadastro.disponibilidades (
    id              UUID        PRIMARY KEY,
    day_of_week     VARCHAR(20) NOT NULL,
    shift           VARCHAR(20) NOT NULL,
    professional_id UUID        NOT NULL REFERENCES cadastro.profissionais_da_saude(id)
);

CREATE TABLE IF NOT EXISTS cadastro.profissional_paciente (
    profissional_id UUID NOT NULL REFERENCES cadastro.profissionais_da_saude(id),
    paciente_id     UUID NOT NULL REFERENCES cadastro.pacientes(id),
    PRIMARY KEY (profissional_id, paciente_id)
);

CREATE TABLE IF NOT EXISTS cadastro.transtornos (
    id   UUID         PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cadastro.cadastros_anuais (
    id          UUID    PRIMARY KEY,
    paciente_id UUID    NOT NULL REFERENCES cadastro.pacientes(id),
    ano        INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS cadastro.cadastro_anual_transtorno (
    cadastro_anual_id UUID NOT NULL REFERENCES cadastro.cadastros_anuais(id),
    transtorno_id     UUID NOT NULL REFERENCES cadastro.transtornos(id),
    PRIMARY KEY (cadastro_anual_id, transtorno_id)
);


-- =============================================================================
-- SCHEMA: atendimento  (módulo de atendimento)
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS atendimento;

-- -----------------------------------------------------------------------------
-- TABELA PRINCIPAL
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS atendimento.atendimento (
    id                    UUID        PRIMARY KEY,
    numeracao             VARCHAR(50) NOT NULL,
    data_atendimento      TIMESTAMP   NOT NULL,
    paciente_id           UUID        NOT NULL,
    profissional_saude_id UUID        NOT NULL
);

-- -----------------------------------------------------------------------------
-- VIEWS  (apontam para as tabelas do schema cadastro)
-- -----------------------------------------------------------------------------

-- vw_pacientes
-- Pacientes não apagados com ao menos um vínculo profissional.
CREATE OR REPLACE VIEW atendimento.vw_pacientes AS
WITH ultimo_cadastro AS (
    SELECT ca.paciente_id, MAX(ca.ano) AS ano
    FROM cadastro.cadastros_anuais ca
    GROUP BY ca.paciente_id
),
responsaveis_por_paciente AS (
    SELECT r.paciente_id,
           ARRAY_AGG(DISTINCT r.nome ORDER BY r.nome) AS responsaveis
    FROM cadastro.responsaveis r
    GROUP BY r.paciente_id
),
transtornos_por_paciente AS (
    SELECT ca.paciente_id,
           ARRAY_AGG(DISTINCT t.nome ORDER BY t.nome) AS transtornos
    FROM cadastro.cadastros_anuais ca
             INNER JOIN ultimo_cadastro uc
                        ON uc.paciente_id = ca.paciente_id AND uc.ano = ca.ano
             INNER JOIN cadastro.cadastro_anual_transtorno cat
                        ON cat.cadastro_anual_id = ca.id
             INNER JOIN cadastro.transtornos t
                        ON t.id = cat.transtorno_id
    GROUP BY ca.paciente_id
)
SELECT
    p.id                 AS paciente_id,
    p.nome_completo      AS nome,
    p.data_de_nascimento AS data_nascimento,
    p.cpf                AS cpf,
    p.contato            AS contato,
    p.is_apagado         AS is_apagado,
    p.endereco_id        AS endereco_id,
    pp.profissional_id   AS profissional_id,
    e.cidade             AS cidade,
    e.rua                AS rua,
    e.bairro             AS bairro,
    e.numero             AS numero_casa,
    COALESCE(rp.responsaveis, ARRAY[]::varchar[]) AS responsaveis,
    COALESCE(tp.transtornos, ARRAY[]::varchar[])  AS transtornos
FROM cadastro.pacientes p
         INNER JOIN cadastro.profissional_paciente pp ON pp.paciente_id = p.id
         INNER JOIN cadastro.enderecos e ON e.id = p.endereco_id
         LEFT JOIN responsaveis_por_paciente rp ON rp.paciente_id = p.id
         LEFT JOIN transtornos_por_paciente tp ON tp.paciente_id = p.id
WHERE p.is_apagado = false;

-- vw_enderecos_paciente
-- Endereços dos pacientes presentes em vw_pacientes.
CREATE OR REPLACE VIEW atendimento.vw_enderecos_paciente AS
SELECT
    vp.paciente_id AS paciente_id,
    e.cidade       AS cidade,
    e.rua          AS rua,
    e.bairro       AS bairro,
    e.numero       AS numero_casa
FROM atendimento.vw_pacientes vp
         INNER JOIN cadastro.enderecos e ON e.id = vp.endereco_id;

-- vw_responsaveis_paciente
-- Responsáveis vinculados a pacientes ativos.
CREATE OR REPLACE VIEW atendimento.vw_responsaveis_paciente AS
SELECT
    r.nome        AS nome,
    r.paciente_id AS paciente_id
FROM cadastro.responsaveis r
         INNER JOIN atendimento.vw_pacientes vp ON vp.paciente_id = r.paciente_id;

-- vw_transtornos_paciente
-- Transtornos concatenados do cadastro anual mais recente por paciente.
CREATE OR REPLACE VIEW atendimento.vw_transtornos_paciente AS
SELECT
    vp.paciente_id                                      AS paciente_id,
    ARRAY_AGG(DISTINCT t.nome ORDER BY t.nome)          AS transtornos
FROM atendimento.vw_pacientes vp
         INNER JOIN cadastro.cadastros_anuais ca
                    ON  ca.paciente_id = vp.paciente_id
                        AND ca.ano = (
                            SELECT MAX(ca2.ano)
                            FROM cadastro.cadastros_anuais ca2
                            WHERE ca2.paciente_id = vp.paciente_id
                        )
         INNER JOIN cadastro.cadastro_anual_transtorno cat ON cat.cadastro_anual_id = ca.id
         INNER JOIN cadastro.transtornos t ON t.id = cat.transtorno_id
GROUP BY vp.paciente_id;

-- vw_profissional_saude
-- Profissional de saúde com sua especialidade.
-- FK: profissionais_da_saude.area_de_atendimento → areas_de_atendimento.area
CREATE OR REPLACE VIEW atendimento.vw_profissional_saude AS
SELECT
    ps.id                  AS profissional_saude_id,
    ps.nome                AS nome,
    ps.email               AS email,
    ps.senha               AS senha,
    ps.perfil              AS perfil,
    ps.contato             AS contato,
    ps.ativo               AS ativo,
    ps.documento_profissional AS registro_profissional,
    ps.area_de_atendimento AS especialidade_id,
    at.area                AS especialidade
FROM cadastro.profissionais_da_saude ps
         INNER JOIN cadastro.areas_de_atendimento at ON at.area = ps.area_de_atendimento;

-- vw_atendimento
-- Atendimentos com referência ao paciente e ao profissional.
CREATE OR REPLACE VIEW atendimento.vw_atendimento AS
SELECT
    a.id                      AS id,
    a.numeracao               AS numeracao,
    a.data_atendimento        AS data_atendimento,
    vp.paciente_id            AS paciente_id,
    vps.profissional_saude_id AS profissional_saude_id
FROM atendimento.atendimento a
         INNER JOIN atendimento.vw_pacientes vp       ON vp.paciente_id            = a.paciente_id
         INNER JOIN atendimento.vw_profissional_saude vps ON vps.profissional_saude_id = a.profissional_saude_id;
