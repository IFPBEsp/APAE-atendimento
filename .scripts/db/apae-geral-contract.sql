BEGIN;

CREATE SCHEMA IF NOT EXISTS apae_geral;
CREATE SCHEMA IF NOT EXISTS gestao_escolar;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS apae_geral.areas_de_atendimento (
    area VARCHAR(255) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS apae_geral.enderecos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cidade VARCHAR(255) NOT NULL,
    cep VARCHAR(255),
    estado VARCHAR(255),
    bairro VARCHAR(255) NOT NULL,
    rua VARCHAR(255) NOT NULL,
    numero VARCHAR(255) NOT NULL,
    complemento VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS apae_geral.usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(255) UNIQUE,
    senha VARCHAR(255),
    nome_completo VARCHAR(255),
    cargo VARCHAR(255) NOT NULL,
    contato VARCHAR(255),
    rg VARCHAR(255) UNIQUE,
    endereco_id UUID REFERENCES apae_geral.enderecos(id),
    primeiro_acesso BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS apae_geral.profissionais_da_saude (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL UNIQUE REFERENCES apae_geral.usuarios(id),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    documento_profissional VARCHAR(255) UNIQUE,
    area_de_atendimento VARCHAR(255) REFERENCES apae_geral.areas_de_atendimento(area)
);

CREATE TABLE IF NOT EXISTS apae_geral.pacientes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_completo VARCHAR(255) NOT NULL,
    data_de_nascimento DATE NOT NULL,
    cpf VARCHAR(255) UNIQUE,
    contato VARCHAR(255) NOT NULL,
    is_aluno BOOLEAN NOT NULL DEFAULT FALSE,
    is_apagado BOOLEAN NOT NULL DEFAULT FALSE,
    endereco_id UUID REFERENCES apae_geral.enderecos(id)
);

CREATE TABLE IF NOT EXISTS apae_geral.responsaveis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paciente_id UUID NOT NULL REFERENCES apae_geral.pacientes(id),
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS apae_geral.transtornos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS apae_geral.cadastros_anuais (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paciente_id UUID NOT NULL REFERENCES apae_geral.pacientes(id),
    ano INTEGER NOT NULL,
    UNIQUE (paciente_id, ano)
);

CREATE TABLE IF NOT EXISTS apae_geral.cadastro_anual_transtorno (
    cadastro_anual_id UUID NOT NULL REFERENCES apae_geral.cadastros_anuais(id),
    transtorno_id UUID NOT NULL REFERENCES apae_geral.transtornos(id),
    PRIMARY KEY (cadastro_anual_id, transtorno_id)
);

CREATE TABLE IF NOT EXISTS apae_geral.agendamentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cadastro_anual_id UUID NOT NULL REFERENCES apae_geral.cadastros_anuais(id),
    profissional_id UUID NOT NULL REFERENCES apae_geral.profissionais_da_saude(id),
    frequencia_dias INTEGER NOT NULL DEFAULT 7,
    hora TIME NOT NULL,
    data_inicial DATE NOT NULL,
    data_final DATE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP,
    substituido_por_id UUID,
    atualizado_de_id UUID
);

CREATE OR REPLACE FUNCTION apae_geral.definir_senha_primeiro_acesso(
    p_usuario_id UUID,
    p_senha_hash TEXT
) RETURNS VOID
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE apae_geral.usuarios
       SET senha = p_senha_hash,
           primeiro_acesso = FALSE
     WHERE id = p_usuario_id;
END;
$$;

COMMIT;
