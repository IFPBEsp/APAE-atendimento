DROP VIEW IF EXISTS atendimento.vw_pacientes;

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
       e.cidade,
       e.rua,
       e.bairro,
       e.numero AS numero_casa,
       COALESCE(rp.responsaveis, ARRAY[]::varchar[]) AS responsaveis,
       COALESCE(tp.transtornos, ARRAY[]::varchar[]) AS transtornos
FROM apae_geral.pacientes p
         INNER JOIN apae_geral.enderecos e
                    ON e.id = p.endereco_id
         LEFT JOIN responsaveis_por_paciente rp
                   ON rp.paciente_id = p.id
         LEFT JOIN transtornos_por_paciente tp
                   ON tp.paciente_id = p.id
WHERE p.is_apagado = false;
