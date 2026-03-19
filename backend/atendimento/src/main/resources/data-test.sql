INSERT INTO profissional_saude
(id, primeiro_nome, nome_completo, email, contato, firebase_uid)
VALUES
('11111111-1111-1111-1111-111111111111', 'Ana', 'Dra. Ana Ribeiro', 'filipekevyn@gmail.com', '11999990001', null),
('22222222-2222-2222-2222-222222222222', 'Marcos', 'Dr. Marcos Oliveira', 'marcosoliveira@gmail.com', '11977770002', null),
('33333333-3333-3333-3333-333333333333', 'Luiz', 'Dr. Luiz Artur', 'luiz.artur.coder@gmail.com', '11977771114', 'Efn0oqQ68rMSjtimpAAQPO3KmjY2'),
('44444444-4444-4444-4444-444444444444', 'Teste', 'Dr. Novo Teste', 'artur.luiz@academico.ifpb.edu.br', '11900000000', 'iA2d7BygJGR30pV3rlxramHkG1t1');

INSERT INTO paciente (id, nome_completo, data_de_nascimento, contato, cpf, responsaveis, cidade, rua, bairro, numero_casa, transtornos) VALUES
( 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'João Pedro Silva', DATE '2016-05-10',
'11988880001', '11122233344', ARRAY['Carlos Silva', 'Fernanda Silva'], 'São Paulo', 'Rua das Flores', 'Centro', 120, ARRAY['TEA', 'TDAH']),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Lucas Souza', DATE '2018-03-15', '11988880002', '22233344455', ARRAY['Mariana Souza'], 'São Paulo', 'Av. Paulista', 'Bela Vista', 1500, ARRAY['TEA']),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Beatriz Santos', DATE '2015-07-22', '11988880003', '33344455566', ARRAY['Ricardo Santos', 'Aline Santos'], 'São Bernardo', 'Rua Bahia', 'Rudge Ramos', 45, ARRAY['TDAH', 'TOD']),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Enzo Gabriel', DATE '2019-11-02', '11988880004', '44455566677', ARRAY['Patrícia Lima'], 'Santo André', 'Rua das Oliveiras', 'Jardim', 12, ARRAY['TEA']);

INSERT INTO profissional_paciente (profissional_id, paciente_id) VALUES
('44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
('33333333-3333-3333-3333-333333333333', 'dddddddd-dddd-dddd-dddd-dddddddddddd');