-- ================================================================
-- Migration: V1__create_views.sql
-- Módulo: Atendimento
-- Schema origem (módulo central): apae
-- Schema destino (este módulo): atendimento
-- Autor: <seu nome>
-- ================================================================


-- ----------------------------------------------------------------
-- 1. vw_pacientes
-- Objetivo: pacientes com vínculo profissional ativo
-- Fonte: apae.pacientes + apae.profissional_paciente
-- Regra: INNER JOIN garante que só retorna pacientes vinculados
-- Sem filtros dinâmicos — is_apagado exposto para o consumidor decidir
-- ----------------------------------------------------------------
CREATE OR REPLACE VIEW atendimento.vw_pacientes AS
SELECT
    p.id                    AS paciente_id,
    p.nome_completo         AS nome,
    p.data_de_nascimento    AS data_nascimento,
    p.cpf                   AS cpf,
    p.contato               AS contato,
    p.is_apagado            AS is_apagado
FROM apae.pacientes p
         INNER JOIN apae.profissional_paciente pp
                    ON pp.paciente_id = p.id;


-- ----------------------------------------------------------------
-- 2. vw_enderecos_paciente
-- Objetivo: endereços dos pacientes presentes em vw_pacientes
-- Fonte: atendimento.vw_pacientes + apae.enderecos
-- Depende: vw_pacientes (deve existir antes)
-- Requer: coluna endereco_id em apae.pacientes
-- ----------------------------------------------------------------
CREATE OR REPLACE VIEW atendimento.vw_enderecos_paciente AS
SELECT
    vp.paciente_id          AS paciente_id,
    e.cidade                AS cidade,
    e.rua                   AS rua,
    e.bairro                AS bairro,
    e.numero                AS numero_casa
FROM atendimento.vw_pacientes vp
         INNER JOIN apae.pacientes p
                    ON p.id = vp.paciente_id
         INNER JOIN apae.enderecos e
                    ON e.id = p.endereco_id;


-- ----------------------------------------------------------------
-- 3. vw_responsaveis_paciente
-- Objetivo: responsáveis vinculados a pacientes em vw_pacientes
-- Fonte: apae.responsaveis + atendimento.vw_pacientes
-- Depende: vw_pacientes (deve existir antes)
-- ----------------------------------------------------------------
CREATE OR REPLACE VIEW atendimento.vw_responsaveis_paciente AS
SELECT
    r.nome                  AS nome,
    r.paciente_id           AS paciente_id
FROM apae.responsaveis r
         INNER JOIN atendimento.vw_pacientes vp
                    ON vp.paciente_id = r.paciente_id;


-- ----------------------------------------------------------------
-- 4. vw_transtornos_paciente
-- Objetivo: alergias + transtornos do cadastro anual mais recente
-- Fonte: vw_pacientes + apae.pacientes (alergias)
--        + apae.cadastros_anuais + apae.cadastro_anual_transtorno
--        + apae.transtornos
-- Depende: vw_pacientes (deve existir antes)
-- Nota: alergias usadas conforme decisão do time
--       transtornos agregados do cadastro anual mais recente (MAX year)
-- ----------------------------------------------------------------
CREATE OR REPLACE VIEW atendimento.vw_transtornos_paciente AS
SELECT
    vp.paciente_id                          AS paciente_id,
    p.alergias                              AS alergias,
    STRING_AGG(t.nome, ', ' ORDER BY t.nome) AS transtornos
FROM atendimento.vw_pacientes vp
         INNER JOIN apae.pacientes p
                    ON p.id = vp.paciente_id
         LEFT JOIN apae.cadastros_anuais ca
                   ON ca.paciente_id = vp.paciente_id
                       AND ca.year = (
                           SELECT MAX(ca2.year)
                           FROM apae.cadastros_anuais ca2
                           WHERE ca2.paciente_id = vp.paciente_id
                       )
         LEFT JOIN apae.cadastro_anual_transtorno cat
                   ON cat.cadastro_anual_id = ca.id
         LEFT JOIN apae.transtornos t
                   ON t.id = cat.transtorno_id
GROUP BY
    vp.paciente_id,
    p.alergias;


-- ----------------------------------------------------------------
-- 5. vw_profissional_saude
-- Objetivo: profissionais com especialidade legível
-- Fonte: apae.profissionais_da_saude + apae.areas_de_atendimento
-- Sem WHERE — filtro de status é responsabilidade do consumidor
-- Nota: FK em areas_de_atendimento.area (não .id) conforme JPA
-- ----------------------------------------------------------------
CREATE OR REPLACE VIEW atendimento.vw_profissional_saude AS
SELECT
    ps.id                           AS profissional_saude_id,
    ps.nome                         AS nome,
    ps.email                        AS email,
    ps.status                       AS status,
    ps.area_de_atendimento          AS especialidade_id,
    at.area                         AS especialidade,
    ps.contato                      AS contato
FROM apae.profissionais_da_saude ps
         LEFT JOIN apae.areas_de_atendimento at
ON at.area = ps.area_de_atendimento;


-- ----------------------------------------------------------------
-- 6. vw_atendimento
-- Objetivo: atendimentos com identificação de paciente e profissional
-- Fonte: atendimento.atendimento (tabela core)
--        + atendimento.vw_pacientes
--        + atendimento.vw_profissional_saude
-- Sem WHERE — sem filtros dinâmicos conforme issue
-- Nota: profissional_id corrigido (issue usava profissional_saude_id
--       mas coluna real na tabela é profissional_id)
-- ----------------------------------------------------------------
CREATE OR REPLACE VIEW atendimento.vw_atendimento AS
SELECT
    a.id                            AS id,
    a.numeracao                     AS numeracao,
    a.data_atendimento              AS data_atendimento,
    vp.paciente_id                  AS paciente_id,
    vps.profissional_saude_id       AS profissional_saude_id
FROM atendimento.atendimento a
         INNER JOIN atendimento.vw_pacientes vp
                    ON vp.paciente_id = a.paciente_id
         INNER JOIN atendimento.vw_profissional_saude vps
                    ON vps.profissional_saude_id = a.profissional_id;