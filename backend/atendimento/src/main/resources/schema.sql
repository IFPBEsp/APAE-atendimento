-- ================================================================
-- Migration: V0__create_schemas.sql
-- Descrição: Criação dos schemas e tabelas simuladas do módulo
--            central (apae) para ambiente de desenvolvimento.
--            Em produção, essas tabelas serão gerenciadas pelo
--            módulo APAE. Este arquivo NÃO deve rodar em produção.
-- ================================================================


-- ----------------------------------------------------------------
-- Schemas
-- ----------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS apae;
CREATE SCHEMA IF NOT EXISTS atendimento;


-- ----------------------------------------------------------------
-- apae.enderecos
-- Compartilhado entre pacientes e profissionais
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.enderecos (
                                              id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              cidade      VARCHAR NOT NULL,
                                              cep         VARCHAR NOT NULL,
                                              estado      VARCHAR NOT NULL,
                                              bairro      VARCHAR NOT NULL,
                                              rua         VARCHAR NOT NULL,
                                              numero      VARCHAR NOT NULL,
                                              complemento VARCHAR
);


-- ----------------------------------------------------------------
-- apae.pacientes
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.pacientes (
                                              id                            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              nome_completo                 VARCHAR NOT NULL,
                                              naturalidade                  VARCHAR NOT NULL,
                                              data_de_nascimento            DATE NOT NULL,
                                              contato                       VARCHAR NOT NULL,
                                              numero_registro_de_nascimento VARCHAR NOT NULL,
                                              cartorio                      VARCHAR NOT NULL,
                                              fls                           VARCHAR NOT NULL,
                                              livro                         VARCHAR NOT NULL,
                                              rg                            VARCHAR NOT NULL UNIQUE,
                                              data_de_emissao               DATE NOT NULL,
                                              orgao_emissor                 VARCHAR NOT NULL,
                                              cpf                           VARCHAR NOT NULL UNIQUE,
                                              cns                           VARCHAR NOT NULL,
                                              nis                           VARCHAR NOT NULL,
                                              data_de_cadastro              DATE NOT NULL,
                                              alergias                      VARCHAR NOT NULL,
                                              is_aluno                      BOOLEAN NOT NULL DEFAULT false,
                                              is_apagado                    BOOLEAN NOT NULL DEFAULT false,
                                              endereco_id                   UUID REFERENCES apae.enderecos(id)
);


-- ----------------------------------------------------------------
-- apae.responsaveis
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.responsaveis (
                                                 id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 nome        VARCHAR NOT NULL,
                                                 contato     VARCHAR NOT NULL,
                                                 parentesco  VARCHAR NOT NULL,
                                                 endereco_id UUID REFERENCES apae.enderecos(id),
                                                 paciente_id UUID NOT NULL REFERENCES apae.pacientes(id)
);


-- ----------------------------------------------------------------
-- apae.areas_de_atendimento
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.areas_de_atendimento (
                                                         id   SERIAL PRIMARY KEY,
                                                         area VARCHAR NOT NULL UNIQUE
);


-- ----------------------------------------------------------------
-- apae.profissionais_da_saude
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.profissionais_da_saude (
                                                           id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                           nome                   VARCHAR NOT NULL,
                                                           email                  VARCHAR NOT NULL UNIQUE,
                                                           contato                VARCHAR NOT NULL,
                                                           documento_profissional VARCHAR UNIQUE,
                                                           rg                     VARCHAR UNIQUE,
                                                           status                 VARCHAR NOT NULL DEFAULT 'ATIVO',
                                                           area_de_atendimento    VARCHAR REFERENCES apae.areas_de_atendimento(area),
                                                           endereco_id            UUID REFERENCES apae.enderecos(id)
);


-- ----------------------------------------------------------------
-- apae.profissional_paciente
-- Tabela de vínculo — base do INNER JOIN da vw_pacientes
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.profissional_paciente (
                                                          profissional_id UUID NOT NULL REFERENCES apae.profissionais_da_saude(id),
                                                          paciente_id     UUID NOT NULL REFERENCES apae.pacientes(id),
                                                          PRIMARY KEY (profissional_id, paciente_id)
);


-- ----------------------------------------------------------------
-- apae.transtornos
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.transtornos (
                                                id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                nome VARCHAR NOT NULL UNIQUE
);


-- ----------------------------------------------------------------
-- apae.cadastros_anuais
-- Um por paciente por ano
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.cadastros_anuais (
                                                     id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     paciente_id UUID NOT NULL REFERENCES apae.pacientes(id),
                                                     year        INTEGER NOT NULL,
                                                     UNIQUE (paciente_id, year)
);


-- ----------------------------------------------------------------
-- apae.cadastro_anual_transtorno
-- Relação N:N entre cadastro anual e transtornos
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apae.cadastro_anual_transtorno (
                                                              cadastro_anual_id UUID NOT NULL REFERENCES apae.cadastros_anuais(id),
                                                              transtorno_id     UUID NOT NULL REFERENCES apae.transtornos(id),
                                                              PRIMARY KEY (cadastro_anual_id, transtorno_id)
);


-- ----------------------------------------------------------------
-- atendimento.atendimento
-- Tabela principal
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS atendimento.atendimento (
                                                       id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                       data_atendimento TIMESTAMP,
                                                       numeracao        BIGINT,
                                                       paciente_id      UUID NOT NULL REFERENCES apae.pacientes(id),
                                                       profissional_id  UUID NOT NULL REFERENCES apae.profissionais_da_saude(id)
);


-- ----------------------------------------------------------------
-- atendimento.credenciais_profissional
-- Autenticação local do módulo de atendimento
-- Separada da view — nunca exposta via vw_profissional_saude
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS atendimento.credenciais_profissional (
                                                                    profissional_id UUID PRIMARY KEY REFERENCES apae.profissionais_da_saude(id),
                                                                    senha           VARCHAR NOT NULL,
                                                                    perfil          VARCHAR NOT NULL DEFAULT 'ROLE_PROFISSIONAL',
                                                                    criado_em       TIMESTAMP NOT NULL DEFAULT now(),
                                                                    atualizado_em   TIMESTAMP NOT NULL DEFAULT now()
);