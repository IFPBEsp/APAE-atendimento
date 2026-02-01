INSERT INTO profissional_saude (id, primeiro_nome, nome_completo, email, contato)
VALUES ( '11111111-1111-1111-1111-111111111111', 'Ana', 'Dra. Ana Ribeiro', 'filipekevyn4@gmail.com', '11999990001'), ('22222222-2222-2222-2222-222222222222', 'Marcos', 'Dr. Marcos Oliveira', 'apaeatendimento2@gmail.com', '11977770002');

INSERT INTO paciente (id, nome_completo, data_de_nascimento, contato, cpf, responsaveis, cidade, rua, bairro, numero_casa, transtornos) VALUES
( 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'João Pedro Silva', DATE '2016-05-10',
'11988880001', '11122233344', ARRAY['Carlos Silva', 'Fernanda Silva'], 'São Paulo', 'Rua das Flores', 'Centro', 120, ARRAY['TEA', 'TDAH']),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Lucas Souza', DATE '2018-03-15', '11988880002', '22233344455', ARRAY['Mariana Souza'], 'São Paulo', 'Av. Paulista', 'Bela Vista', 1500, ARRAY['TEA']),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Beatriz Santos', DATE '2015-07-22', '11988880003', '33344455566', ARRAY['Ricardo Santos', 'Aline Santos'], 'São Bernardo', 'Rua Bahia', 'Rudge Ramos', 45, ARRAY['TDAH', 'TOD']),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Enzo Gabriel', DATE '2019-11-02', '11988880004', '44455566677', ARRAY['Patrícia Lima'], 'Santo André', 'Rua das Oliveiras', 'Jardim', 12, ARRAY['TEA']);

INSERT INTO profissional_paciente (profissional_id, paciente_id) VALUES
('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
('22222222-2222-2222-2222-222222222222', 'dddddddd-dddd-dddd-dddd-dddddddddddd');
