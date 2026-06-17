DROP VIEW IF EXISTS atendimento.vw_pacientes;
DROP VIEW IF EXISTS atendimento.vw_profissional_saude;

ALTER TABLE atendimento.profissional_paciente
DROP CONSTRAINT IF EXISTS fk_pp_profissional;

ALTER TABLE atendimento.atendimento
DROP CONSTRAINT IF EXISTS fk_atend_profissional;

ALTER TABLE atendimento.agendamento
DROP CONSTRAINT IF EXISTS fk_agend_profissional;

ALTER TABLE atendimento.anexo
DROP CONSTRAINT IF EXISTS fk_anexo_prof;

ALTER TABLE atendimento.atendimento
ALTER COLUMN data_atendimento TYPE TIMESTAMP
    USING data_atendimento::timestamp;

ALTER TABLE atendimento.atendimento
    ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE atendimento.profissional_paciente
    ADD CONSTRAINT fk_pp_profissional
        FOREIGN KEY (profissional_id)
            REFERENCES apae_geral.profissionais_da_saude(id);

ALTER TABLE atendimento.atendimento
    ADD CONSTRAINT fk_atend_profissional
        FOREIGN KEY (profissional_id)
            REFERENCES apae_geral.profissionais_da_saude(id);

ALTER TABLE atendimento.agendamento
    ADD CONSTRAINT fk_agend_profissional
        FOREIGN KEY (profissional_id)
            REFERENCES apae_geral.profissionais_da_saude(id);

ALTER TABLE atendimento.anexo
    ADD CONSTRAINT fk_anexo_prof
        FOREIGN KEY (profissional_id)
            REFERENCES apae_geral.profissionais_da_saude(id);

CREATE OR REPLACE VIEW atendimento.vw_pacientes AS
WITH ultimo_cadastro AS (
   SELECT ca.paciente_id, MAX(ca.ano) AS ano
   FROM apae_geral.cadastros_anuais ca
   GROUP BY ca.paciente_id
),
responsaveis_por_paciente AS (
   SELECT r.paciente_id,
          ARRAY_AGG(DISTINCT r.nome ORDER BY r.nome) AS responsaveis
   FROM apae_geral.responsaveis r
   GROUP BY r.paciente_id
),
transtornos_por_paciente AS (
   SELECT ca.paciente_id,
          ARRAY_AGG(DISTINCT t.nome ORDER BY t.nome) AS transtornos
   FROM apae_geral.cadastros_anuais ca
   INNER JOIN ultimo_cadastro uc
          ON uc.paciente_id = ca.paciente_id AND uc.ano = ca.ano
   INNER JOIN apae_geral.cadastro_anual_transtorno cat
          ON cat.cadastro_anual_id = ca.id
   INNER JOIN apae_geral.transtornos t
          ON t.id = cat.transtorno_id
   GROUP BY ca.paciente_id
)
SELECT p.id AS paciente_id,
       p.nome_completo AS nome,
       p.data_de_nascimento AS data_nascimento,
       p.cpf,
       p.contato,
       pp.profissional_id,
       e.cidade,
       e.rua,
       e.bairro,
       e.numero AS numero_casa,
       COALESCE(rp.responsaveis, ARRAY[]::varchar[]) AS responsaveis,
       COALESCE(tp.transtornos, ARRAY[]::varchar[]) AS transtornos
FROM apae_geral.pacientes p
         INNER JOIN atendimento.profissional_paciente pp
                    ON pp.paciente_id = p.id
         INNER JOIN apae_geral.enderecos e
                    ON e.id = p.endereco_id
         LEFT JOIN responsaveis_por_paciente rp
                   ON rp.paciente_id = p.id
         LEFT JOIN transtornos_por_paciente tp
                   ON tp.paciente_id = p.id
WHERE p.is_apagado = false;

CREATE OR REPLACE VIEW atendimento.vw_profissional_saude AS
SELECT pds.id AS profissional_saude_id,
       u.nome_completo AS nome,
       u.email,
       u.senha,
       u.cargo AS perfil,
       u.contato,
       pds.ativo AS ativo,
       pds.documento_profissional AS registro_profissional,
       aa.area AS especialidade
FROM apae_geral.usuarios u
         INNER JOIN apae_geral.profissionais_da_saude pds
                    ON pds.usuario_id = u.id
         INNER JOIN apae_geral.areas_de_atendimento aa
                    ON aa.area = pds.area_de_atendimento;