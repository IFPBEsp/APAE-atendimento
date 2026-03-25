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
                                            transtornos VARCHAR ARRAY
);

CREATE TABLE IF NOT EXISTS vw_profissionais (
                                                id UUID PRIMARY KEY,
                                                nome VARCHAR(255) NOT NULL,
                                                registro_profissional VARCHAR(50),
                                                especialidade VARCHAR(255),
                                                firebase_uid VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profissional_paciente (
                                                     profissional_id UUID REFERENCES vw_profissionais(id),
                                                     paciente_id UUID REFERENCES vw_pacientes(id),
                                                     PRIMARY KEY (profissional_id, paciente_id)
);