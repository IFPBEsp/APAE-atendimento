DELETE FROM profissional_paciente;
DELETE FROM vw_profissionais;
DELETE FROM vw_pacientes;

INSERT INTO vw_profissionais (id, nome, registro_profissional, especialidade, firebase_uid)
VALUES
    ('22222222-2222-2222-2222-222222222222', 'Dr. Marcos Oliveira', 'CRM-SP 12345', 'Clínico Geral', null),
    ('33333333-3333-3333-3333-333333333333', 'Dr. Luiz Artur', 'CRP-SP 67890', 'Psicólogo', 'Efn0oqQ68rMSjtimpAAQPO3KmjY2'),
    ('44444444-4444-4444-4444-444444444444', 'Dra. Ana Ribeiro', 'CREFITO 54321', 'Fisioterapeuta', null);

INSERT INTO vw_pacientes (id, nome, cpf, data_nascimento) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'João Pedro Silva', '11122233344', DATE '2016-05-10'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Lucas Souza', '22233344455', DATE '2018-03-15'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Beatriz Santos', '33344455566', DATE '2015-07-22'),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Enzo Gabriel', '44455566677', DATE '2019-11-02');

-- A tabela de relacionamento ainda é necessária e válida!
INSERT INTO profissional_paciente (profissional_id, paciente_id) VALUES
      ('44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
      ('44444444-4444-4444-4444-444444444444', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
      ('22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
      ('33333333-3333-3333-3333-333333333333', 'dddddddd-dddd-dddd-dddd-dddddddddddd');