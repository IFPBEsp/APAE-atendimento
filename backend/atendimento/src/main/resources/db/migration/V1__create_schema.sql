CREATE SCHEMA IF NOT EXISTS atendimento;
CREATE EXTENSION IF NOT EXISTS pgcrypto;


CREATE TABLE IF NOT EXISTS atendimento.tipo_arquivo (
    id   BIGINT PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL
    );


CREATE TABLE IF NOT EXISTS atendimento.profissional_paciente (
    profissional_id UUID NOT NULL,
    paciente_id     UUID NOT NULL,
    PRIMARY KEY (profissional_id, paciente_id),
    CONSTRAINT fk_pp_profissional FOREIGN KEY (profissional_id)
        REFERENCES apae_geral.usuarios(id),
    CONSTRAINT fk_pp_paciente FOREIGN KEY (paciente_id)
        REFERENCES apae_geral.pacientes(id)
    );


CREATE TABLE IF NOT EXISTS atendimento.atendimento (
    id                UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    numeracao         VARCHAR(50) NOT NULL,
    data_atendimento  DATE        NOT NULL,
    paciente_id       UUID        NOT NULL,
    profissional_id   UUID        NOT NULL,
    CONSTRAINT fk_atend_paciente     FOREIGN KEY (paciente_id)
        REFERENCES apae_geral.pacientes(id),
    CONSTRAINT fk_atend_profissional FOREIGN KEY (profissional_id)
        REFERENCES apae_geral.usuarios(id)
    );


CREATE TABLE IF NOT EXISTS atendimento.topico (
    id             UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    atendimento_id UUID         NOT NULL,
    ordem          INTEGER      NOT NULL,
    titulo         VARCHAR(255) NOT NULL,
    descricao      TEXT         NOT NULL,
    CONSTRAINT fk_topico_atend FOREIGN KEY (atendimento_id)
        REFERENCES atendimento.atendimento(id)
    );


CREATE TABLE IF NOT EXISTS atendimento.agendamento (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    numeracao       VARCHAR(50) NOT NULL,
    status          BOOLEAN     NOT NULL DEFAULT FALSE,
    data_hora       TIMESTAMP   NOT NULL,
    profissional_id UUID        NOT NULL,
    paciente_id     UUID        NOT NULL,
    CONSTRAINT fk_agend_profissional FOREIGN KEY (profissional_id)
        REFERENCES apae_geral.usuarios(id),
    CONSTRAINT fk_agend_paciente FOREIGN KEY (paciente_id)
        REFERENCES apae_geral.pacientes(id)
    );


CREATE TABLE IF NOT EXISTS atendimento.anexo (
    object_name      VARCHAR(255) PRIMARY KEY,
    nome_arquivo     VARCHAR(255) NOT NULL,
    data             DATE         NOT NULL,
    titulo           VARCHAR(255) NOT NULL,
    descricao        TEXT,
    titulo_canonical VARCHAR(255),
    tipo_id          BIGINT       NOT NULL,
    profissional_id  UUID         NOT NULL,
    paciente_id      UUID         NOT NULL,
    CONSTRAINT fk_anexo_tipo  FOREIGN KEY (tipo_id)
        REFERENCES atendimento.tipo_arquivo(id),
    CONSTRAINT fk_anexo_prof  FOREIGN KEY (profissional_id)
        REFERENCES apae_geral.usuarios(id),
    CONSTRAINT fk_anexo_pac   FOREIGN KEY (paciente_id)
        REFERENCES apae_geral.pacientes(id)
    );


-- Views
CREATE OR REPLACE VIEW atendimento.vw_pacientes AS
WITH ultimo_cadastro AS (
   SELECT ca.paciente_id, MAX(ca.ano) AS ano
   FROM apae_geral.cadastros_anuais ca GROUP BY ca.paciente_id
),
transtornos_por_paciente AS (
   SELECT ca.paciente_id,
          STRING_AGG(t.nome, ', ' ORDER BY t.nome) AS transtornos
   FROM apae_geral.cadastros_anuais ca
   INNER JOIN ultimo_cadastro uc
          ON uc.paciente_id = ca.paciente_id AND uc.ano = ca.ano
   INNER JOIN apae_geral.cadastro_anual_transtorno cat
          ON cat.cadastro_anual_id = ca.id
   INNER JOIN apae_geral.transtornos t ON t.id = cat.transtorno_id
   GROUP BY ca.paciente_id
)
SELECT p.id AS paciente_id, p.nome_completo AS nome,
       p.data_de_nascimento AS data_nascimento, p.cpf,
       p.contato, p.is_apagado, pp.profissional_id,
       e.cidade, e.rua, e.bairro, e.numero AS numero_casa,
       r.nome AS nome_responsavel, tp.transtornos
FROM apae_geral.pacientes p
         INNER JOIN atendimento.profissional_paciente pp ON pp.paciente_id = p.id
         INNER JOIN apae_geral.enderecos e ON e.id = p.endereco_id
         INNER JOIN apae_geral.responsaveis r ON r.paciente_id = p.id
         INNER JOIN transtornos_por_paciente tp ON tp.paciente_id = p.id
WHERE p.is_apagado = false;


CREATE OR REPLACE VIEW atendimento.vw_profissional_saude AS
SELECT u.id AS profissional_saude_id,
       u.nome_completo AS nome, u.email,
       pds.ativo AS status, aa.area AS especialidade, u.contato
FROM apae_geral.usuarios u
         INNER JOIN apae_geral.profissionais_da_saude pds ON pds.usuario_id = u.id
         INNER JOIN apae_geral.areas_de_atendimento aa
                    ON aa.area = pds.area_de_atendimento;