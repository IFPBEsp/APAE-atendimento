CREATE SCHEMA IF NOT EXISTS apae_geral;

CREATE TABLE IF NOT EXISTS apae_geral.areas_de_atendimento (
                                                               area VARCHAR(255) PRIMARY KEY
    );

CREATE TABLE IF NOT EXISTS apae_geral.usuarios (
                                                   id UUID PRIMARY KEY,
                                                   nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255),
    cargo VARCHAR(100),
    contato VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS apae_geral.profissionais_da_saude (
                                                                 id UUID PRIMARY KEY,
                                                                 usuario_id UUID NOT NULL REFERENCES apae_geral.usuarios(id),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    documento_profissional VARCHAR(100),
    area_de_atendimento VARCHAR(255) REFERENCES apae_geral.areas_de_atendimento(area)
    );

CREATE TABLE IF NOT EXISTS apae_geral.enderecos (
                                                    id UUID PRIMARY KEY,
                                                    cidade VARCHAR(255),
    rua VARCHAR(255),
    bairro VARCHAR(255),
    numero INTEGER
    );

CREATE TABLE IF NOT EXISTS apae_geral.pacientes (
                                                    id UUID PRIMARY KEY,
                                                    nome_completo VARCHAR(255) NOT NULL,
    data_de_nascimento DATE,
    cpf VARCHAR(20),
    contato VARCHAR(50),
    is_apagado BOOLEAN NOT NULL DEFAULT FALSE,
    endereco_id UUID REFERENCES apae_geral.enderecos(id)
    );

CREATE TABLE IF NOT EXISTS apae_geral.responsaveis (
                                                       id UUID PRIMARY KEY,
                                                       paciente_id UUID NOT NULL REFERENCES apae_geral.pacientes(id),
    nome VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS apae_geral.transtornos (
                                                      id UUID PRIMARY KEY,
                                                      nome VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS apae_geral.cadastros_anuais (
                                                           id UUID PRIMARY KEY,
                                                           paciente_id UUID NOT NULL REFERENCES apae_geral.pacientes(id),
    ano INTEGER NOT NULL
    );

CREATE TABLE IF NOT EXISTS apae_geral.cadastro_anual_transtorno (
                                                                    cadastro_anual_id UUID NOT NULL REFERENCES apae_geral.cadastros_anuais(id),
    transtorno_id UUID NOT NULL REFERENCES apae_geral.transtornos(id),
    PRIMARY KEY (cadastro_anual_id, transtorno_id)
    );