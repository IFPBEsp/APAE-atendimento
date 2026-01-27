INSERT INTO profissional_saude (id, primeiro_nome, nome_completo, email, contato)
VALUES ( '11111111-1111-1111-1111-111111111111', 'Ana', 'Dra. Ana Ribeiro', 'ana.ribeiro@apae.org.br', '11999990001');

INSERT INTO paciente (id, nome_completo, data_de_nascimento, contato, cpf, responsaveis, cidade, rua, bairro, numero_casa, transtornos) VALUES
( 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'João Pedro Silva', DATE '2016-05-10',
'11988880001', '11122233344', ARRAY['Carlos Silva', 'Fernanda Silva'], 'São Paulo', 'Rua das Flores', 'Centro', 120, ARRAY['TEA', 'TDAH']
);

INSERT INTO profissional_paciente (profissional_id, paciente_id) VALUES
('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa')