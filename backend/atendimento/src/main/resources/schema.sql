-- Limpeza do banco de dados
DROP TABLE IF EXISTS anexo CASCADE;
DROP TABLE IF EXISTS tipo_arquivo CASCADE;
DROP TABLE IF EXISTS agendamento CASCADE;
DROP TABLE IF EXISTS topico CASCADE;
DROP TABLE IF EXISTS atendimento CASCADE;
DROP TABLE IF EXISTS profissional_paciente CASCADE;
DROP TABLE IF EXISTS vw_pacientes CASCADE;
DROP TABLE IF EXISTS vw_profissionais CASCADE;

-- Tabela de Pacientes (Mapeada da View de Integração)
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

-- Tabela de Profissionais (Mapeada da View de Integração)
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

-- Tabela de Relacionamento entre Profissionais e Pacientes
CREATE TABLE IF NOT EXISTS profissional_paciente (
    profissional_id UUID REFERENCES vw_profissionais(id),
    paciente_id UUID REFERENCES vw_pacientes(id),
    PRIMARY KEY (profissional_id, paciente_id)
);

-- Tabela de Atendimentos
CREATE TABLE IF NOT EXISTS atendimento (
    id UUID PRIMARY KEY,
    data_atendimento TIMESTAMP NOT NULL,
    numeracao BIGINT,
    paciente_id UUID NOT NULL REFERENCES vw_pacientes(id),
    profissional_id UUID NOT NULL REFERENCES vw_profissionais(id)
);

-- Tabela de Tópicos do Relatório de Atendimento
CREATE TABLE IF NOT EXISTS topico (
     id UUID PRIMARY KEY,
     titulo VARCHAR(255) NOT NULL,
     descricao TEXT NOT NULL,
     ordem INTEGER,
     atendimento_id UUID NOT NULL REFERENCES atendimento(id)
);

-- Tabela de Agendamentos
CREATE TABLE IF NOT EXISTS agendamento (
    id UUID PRIMARY KEY,
    status BOOLEAN DEFAULT FALSE,
    data_hora TIMESTAMP NOT NULL,
    numeracao BIGINT,
    profissional_id UUID NOT NULL REFERENCES vw_profissionais(id),
    paciente_id UUID NOT NULL REFERENCES vw_pacientes(id)
);

-- Tabela de Tipos de Arquivo (Anexo)
CREATE TABLE IF NOT EXISTS tipo_arquivo (
    id BIGINT PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL
);

-- Tabela de Anexos (Arquivos)
CREATE TABLE IF NOT EXISTS anexo (
    object_name VARCHAR(255) PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    data DATE NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    titulo_canonical VARCHAR(255),
    tipo_id BIGINT NOT NULL REFERENCES tipo_arquivo(id),
    profissional_id UUID NOT NULL REFERENCES vw_profissionais(id),
    paciente_id UUID NOT NULL REFERENCES vw_pacientes(id)
);
