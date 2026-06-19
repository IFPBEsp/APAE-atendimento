INSERT INTO apae_geral.areas_de_atendimento (area)
VALUES ('Psicologia')
    ON CONFLICT (area) DO NOTHING;

INSERT INTO apae_geral.usuarios (
    id,
    nome_completo,
    email,
    senha,
    cargo,
    contato
)
VALUES (
           '44444444-4444-4444-4444-444444444444',
           'Dr. Luiz Artur',
           'teste@gmail.com',
           '$2a$10$05v1Sk1c9CnRWX9wOMcYP.eI0buevR1DltWhnE3MUA4Nv5IKDX60O',
           'ROLE_PROFISSIONAL',
           '(83) 99999-0000'
       )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO apae_geral.profissionais_da_saude (
    id,
    usuario_id,
    ativo,
    documento_profissional,
    area_de_atendimento
)
VALUES (
           '44444444-4444-4444-4444-444444444444',
           '44444444-4444-4444-4444-444444444444',
           TRUE,
           'CRP-123',
           'Psicologia'
       )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO apae_geral.enderecos (
    id,
    cidade,
    rua,
    bairro,
    numero
)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'Campina Grande',
        'Rua das Flores',
        'Centro',
        123
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'Areia',
        'Rua Projetada',
        'Morro',
        22
    )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO apae_geral.pacientes (
    id,
    nome_completo,
    data_de_nascimento,
    cpf,
    contato,
    is_apagado,
    endereco_id
)
VALUES
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'Joao Pedro Silva',
        DATE '2016-05-10',
        '111.222.333-44',
        '(83) 98888-7777',
        FALSE,
        '11111111-1111-1111-1111-111111111111'
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        'Lucas Souza',
        DATE '2018-03-15',
        '22233344455',
        '(83) 84444-3333',
        FALSE,
        '22222222-2222-2222-2222-222222222222'
    )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO apae_geral.responsaveis (
    id,
    paciente_id,
    nome
)
VALUES
    (
        '33333333-3333-3333-3333-333333333331',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'Maria Silva'
    ),
    (
        '33333333-3333-3333-3333-333333333332',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'Jose Silva'
    ),
    (
        '33333333-3333-3333-3333-333333333333',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        'Rafaela Santos'
    )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO apae_geral.transtornos (
    id,
    nome
)
VALUES
    ('55555555-5555-5555-5555-555555555551', 'TEA'),
    ('55555555-5555-5555-5555-555555555552', 'TDAH')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO apae_geral.cadastros_anuais (
    id,
    paciente_id,
    ano
)
VALUES
    (
        '66666666-6666-6666-6666-666666666661',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        2026
    ),
    (
        '66666666-6666-6666-6666-666666666662',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        2026
    )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO apae_geral.cadastro_anual_transtorno (
    cadastro_anual_id,
    transtorno_id
)
VALUES
    (
        '66666666-6666-6666-6666-666666666661',
        '55555555-5555-5555-5555-555555555551'
    ),
    (
        '66666666-6666-6666-6666-666666666661',
        '55555555-5555-5555-5555-555555555552'
    ),
    (
        '66666666-6666-6666-6666-666666666662',
        '55555555-5555-5555-5555-555555555552'
    )
    ON CONFLICT DO NOTHING;

INSERT INTO atendimento.profissional_paciente (
    profissional_id,
    paciente_id
)
VALUES
    (
        '44444444-4444-4444-4444-444444444444',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
    ),
    (
        '44444444-4444-4444-4444-444444444444',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'
    )
    ON CONFLICT DO NOTHING;