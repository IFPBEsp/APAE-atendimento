DROP TABLE IF EXISTS topico CASCADE;
DROP TABLE IF EXISTS atendimento CASCADE;
DROP TABLE IF EXISTS profissional_paciente CASCADE;
DROP TABLE IF EXISTS vw_pacientes CASCADE;
DROP TABLE IF EXISTS vw_profissionais CASCADE;

CREATE TABLE IF NOT EXISTS vw_pacientes (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    data_nascimento DATE NOT NULL,
    contato VARCHAR(20),
    cidade VARCHAR(100),
    rua VARCHAR(255),
    bairro VARCHAR(100),
    numero_casa INTEGER,
    responsaveis VARCHAR ARRAY,
    transtornos VARCHAR ARRAY,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS vw_profissionais (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    registro_profissional VARCHAR(50),
    especialidade VARCHAR(255),
    email VARCHAR(255),
    senha VARCHAR(255),
    perfil VARCHAR(100),
    status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS profissional_paciente (
    profissional_id UUID REFERENCES vw_profissionais(id),
    paciente_id UUID REFERENCES vw_pacientes(id),
    PRIMARY KEY (profissional_id, paciente_id)
);

CREATE TABLE IF NOT EXISTS atendimento (
    id UUID PRIMARY KEY,
    data_atendimento TIMESTAMP NOT NULL,
    numeracao BIGINT,
    paciente_id UUID NOT NULL REFERENCES vw_pacientes(id),
    profissional_id UUID NOT NULL REFERENCES vw_profissionais(id)
);

CREATE TABLE IF NOT EXISTS topico (
     id UUID PRIMARY KEY,
     titulo VARCHAR(255) NOT NULL,
     descricao TEXT NOT NULL,
     ordem INTEGER,
     atendimento_id UUID NOT NULL REFERENCES atendimento(id)
);