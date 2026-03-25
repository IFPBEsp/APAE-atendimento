INSERT INTO vw_pacientes (id, nome, cpf, data_nascimento, contato, cidade, rua, bairro, numero_casa, responsaveis, transtornos)
VALUES (
           'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
           'João Pedro Silva',
           '111.222.333-44',
           '2016-05-10',
           '(83) 98888-7777',
           'Campina Grande',
           'Rua das Flores',
           'Centro',
           123,
           ARRAY['Maria Silva', 'José Silva'],
           ARRAY['TEA', 'TDAH'] ),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     'Lucas Souza',
     '22233344455',
     DATE '2018-03-15',
     '(83)84444-3333',
     'Areia',
     'rua projetada',
     'morro',
     '022',
    ARRAY['Rafaela santos', 'Francisco Alvez'],
     ARRAY['TDA']);

INSERT INTO vw_profissionais (id, nome, registro_profissional, especialidade, firebase_uid)
VALUES ('44444444-4444-4444-4444-444444444444', 'Dr. Luiz Artur', 'CRP-123', 'Psicólogo', 'Efn0oqQ68rMSjtimpAAQPO3KmjY2');

INSERT INTO profissional_paciente (profissional_id, paciente_id) VALUES
      ('44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
      ('44444444-4444-4444-4444-444444444444', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');