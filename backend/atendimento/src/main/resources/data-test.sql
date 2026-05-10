/* Deprecated */

INSERT INTO vw_pacientes (id, nome, cpf, data_nascimento, contato, cidade, rua, bairro, numero_casa, responsaveis, transtornos, ativo)
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
           ARRAY['TEA', 'TDAH'],
           true
       ),
       (
           'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
           'Lucas Souza',
           '22233344455',
           DATE '2018-03-15',
           '(83)84444-3333',
           'Areia',
           'rua projetada',
           'morro',
           '022',
           ARRAY['Rafaela santos', 'Francisco Alvez'],
           ARRAY['TDA'],
           true
       );

INSERT INTO vw_profissionais (id, nome, registro_profissional, especialidade, email, senha, perfil, status)
VALUES (
           '44444444-4444-4444-4444-444444444444',
           'Dr. Luiz Artur',
           'CRP-123',
           'Psicólogo',
           'teste@gmail.com',
           '$2a$10$05v1Sk1c9CnRWX9wOMcYP.eI0buevR1DltWhnE3MUA4Nv5IKDX60O',
           'ROLE_PROFISSIONAL',
           'ATIVO'
       );


INSERT INTO profissional_paciente (profissional_id, paciente_id)
VALUES
    ('44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    ('44444444-4444-4444-4444-444444444444', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');