BEGIN;

INSERT INTO apae_geral.areas_de_atendimento (area)
VALUES ('Psicologia'), ('Fisioterapia')
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.enderecos (id, cidade, cep, estado, bairro, rua, numero)
VALUES
    ('a1000000-0000-4000-8000-000000000001', 'Esperanca', '58135-000', 'PB', 'Centro', 'Rua Local Um', '100'),
    ('a1000000-0000-4000-8000-000000000002', 'Esperanca', '58135-000', 'PB', 'Centro', 'Rua Local Dois', '200')
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.usuarios
    (id, email, cpf, senha, nome_completo, cargo, contato, rg, endereco_id, primeiro_acesso, ativo)
VALUES
    (
        'a2000000-0000-4000-8000-000000000001',
        'profissional@teste.local',
        '000.000.010-82',
        crypt('12345678', gen_salt('bf', 10)),
        'Profissional de Atendimento Ficticio',
        'ATENDIMENTO',
        '(83) 90000-0010',
        'LOCAL-ATEND-01',
        'a1000000-0000-4000-8000-000000000001',
        FALSE,
        TRUE
    )
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.profissionais_da_saude
    (id, usuario_id, ativo, documento_profissional, area_de_atendimento)
VALUES
    (
        'a3000000-0000-4000-8000-000000000001',
        'a2000000-0000-4000-8000-000000000001',
        TRUE,
        'CRP-LOCAL-ATEND-01',
        'Psicologia'
    )
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.pacientes
    (id, nome_completo, data_de_nascimento, cpf, contato, is_aluno, is_apagado, endereco_id)
VALUES
    ('a4000000-0000-4000-8000-000000000001', 'Paciente Ficticio Um', DATE '2014-05-10', '000.000.110-82', '(83) 90000-1010', TRUE, FALSE, 'a1000000-0000-4000-8000-000000000002'),
    ('a4000000-0000-4000-8000-000000000002', 'Paciente Ficticio Dois', DATE '2016-08-20', '000.000.111-63', '(83) 90000-1011', FALSE, FALSE, 'a1000000-0000-4000-8000-000000000002')
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.responsaveis (id, paciente_id, nome)
VALUES
    ('a4100000-0000-4000-8000-000000000001', 'a4000000-0000-4000-8000-000000000001', 'Responsavel Ficticio Um'),
    ('a4100000-0000-4000-8000-000000000002', 'a4000000-0000-4000-8000-000000000002', 'Responsavel Ficticio Dois')
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.transtornos (id, nome)
VALUES
    ('a5000000-0000-4000-8000-000000000001', 'Transtorno ficticio A'),
    ('a5000000-0000-4000-8000-000000000002', 'Transtorno ficticio B')
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.cadastros_anuais (id, paciente_id, ano)
VALUES
    ('a6000000-0000-4000-8000-000000000001', 'a4000000-0000-4000-8000-000000000001', EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER),
    ('a6000000-0000-4000-8000-000000000002', 'a4000000-0000-4000-8000-000000000002', EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER)
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.cadastro_anual_transtorno (cadastro_anual_id, transtorno_id)
VALUES
    ('a6000000-0000-4000-8000-000000000001', 'a5000000-0000-4000-8000-000000000001'),
    ('a6000000-0000-4000-8000-000000000002', 'a5000000-0000-4000-8000-000000000002')
ON CONFLICT DO NOTHING;

INSERT INTO apae_geral.agendamentos
    (id, cadastro_anual_id, profissional_id, frequencia_dias, hora, data_inicial, data_final, ativo, data_criacao)
VALUES
    (
        'a7000000-0000-4000-8000-000000000001',
        'a6000000-0000-4000-8000-000000000001',
        'a3000000-0000-4000-8000-000000000001',
        7,
        TIME '09:00',
        CURRENT_DATE,
        CURRENT_DATE + 28,
        TRUE,
        CURRENT_TIMESTAMP
    )
ON CONFLICT DO NOTHING;

INSERT INTO atendimento.profissional_paciente (profissional_id, paciente_id)
VALUES
    ('a3000000-0000-4000-8000-000000000001', 'a4000000-0000-4000-8000-000000000001'),
    ('a3000000-0000-4000-8000-000000000001', 'a4000000-0000-4000-8000-000000000002')
ON CONFLICT DO NOTHING;

INSERT INTO atendimento.agendamento
    (id, numeracao, status, data_hora, profissional_id, paciente_id)
VALUES
    (
        'a8000000-0000-4000-8000-000000000001',
        'AG-LOCAL-001',
        FALSE,
        CURRENT_TIMESTAMP + INTERVAL '2 days',
        'a3000000-0000-4000-8000-000000000001',
        'a4000000-0000-4000-8000-000000000001'
    )
ON CONFLICT DO NOTHING;

INSERT INTO atendimento.atendimento
    (id, numeracao, data_atendimento, paciente_id, profissional_id, status)
VALUES
    (
        'a9000000-0000-4000-8000-000000000001',
        'AT-LOCAL-001',
        CURRENT_TIMESTAMP - INTERVAL '1 day',
        'a4000000-0000-4000-8000-000000000001',
        'a3000000-0000-4000-8000-000000000001',
        TRUE
    )
ON CONFLICT DO NOTHING;

INSERT INTO atendimento.topico (id, atendimento_id, ordem, titulo, descricao)
VALUES
    (
        'aa000000-0000-4000-8000-000000000001',
        'a9000000-0000-4000-8000-000000000001',
        1,
        'Evolucao ficticia',
        'Registro criado exclusivamente para desenvolvimento local.'
    )
ON CONFLICT DO NOTHING;

COMMIT;
