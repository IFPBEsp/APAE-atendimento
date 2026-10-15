INSERT INTO apae_geral.areas_de_atendimento (area)
VALUES ('Psicologia')
ON CONFLICT (area) DO NOTHING;

INSERT INTO apae_geral.usuarios (id, nome_completo, email, senha, cargo, contato, cpf, primeiro_acesso)
VALUES ('c0000000-0000-0000-0000-000000000001', 'Dra. Repo Teste', 'repo.teste.1@apae.org', 'x',
        'ROLE_PROFISSIONAL', '(83) 90000-0001', '900.000.000-01', FALSE),
       ('c0000000-0000-0000-0000-000000000002', 'Dr. Somente Inativo', 'repo.teste.2@apae.org', 'x',
        'ROLE_PROFISSIONAL', '(83) 90000-0002', '900.000.000-02', FALSE),
       ('c0000000-0000-0000-0000-000000000003', 'Dra. Sem Agenda', 'repo.teste.3@apae.org', 'x',
        'ROLE_PROFISSIONAL', '(83) 90000-0003', '900.000.000-03', FALSE);

INSERT INTO apae_geral.profissionais_da_saude (id, usuario_id, ativo, documento_profissional, area_de_atendimento)
VALUES ('d0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', TRUE, 'CRP-REPO-1', 'Psicologia'),
       ('d0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000002', TRUE, 'CRP-REPO-2', 'Psicologia'),
       ('d0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000003', TRUE, 'CRP-REPO-3', 'Psicologia');

INSERT INTO apae_geral.pacientes (id, nome_completo, data_de_nascimento, cpf, contato, is_apagado)
VALUES ('e0000000-0000-0000-0000-000000000001', 'Paciente Repositorio Teste', DATE '2015-01-01',
        '900.111.222-33', '(83) 90000-1111', FALSE);

INSERT INTO apae_geral.cadastros_anuais (id, paciente_id, ano)
VALUES ('f0000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000001', 2030);

INSERT INTO apae_geral.agendamentos
    (id, cadastro_anual_id, profissional_id, frequencia_dias, hora, data_inicial, data_final, ativo)
VALUES ('a0000000-0000-0000-0000-00000000000a', 'f0000000-0000-0000-0000-000000000001',
        'd0000000-0000-0000-0000-000000000001', 7, TIME '10:30', DATE '2030-03-01', DATE '2030-03-15', TRUE),
       ('a0000000-0000-0000-0000-00000000000b', 'f0000000-0000-0000-0000-000000000001',
        'd0000000-0000-0000-0000-000000000001', 7, TIME '14:00', DATE '2030-03-08', DATE '2030-03-08', FALSE),
       ('a0000000-0000-0000-0000-00000000000c', 'f0000000-0000-0000-0000-000000000001',
        'd0000000-0000-0000-0000-000000000002', 7, TIME '08:00', DATE '2030-03-08', DATE '2030-03-08', FALSE),
       ('a0000000-0000-0000-0000-00000000000d', 'f0000000-0000-0000-0000-000000000001',
        'd0000000-0000-0000-0000-000000000001', 7, TIME '16:00', DATE '2030-04-10', NULL, TRUE),
       ('a0000000-0000-0000-0000-00000000000e', 'f0000000-0000-0000-0000-000000000001',
        'd0000000-0000-0000-0000-000000000001', 7, TIME '14:00', DATE '2030-03-08', DATE '2030-03-08', TRUE);
