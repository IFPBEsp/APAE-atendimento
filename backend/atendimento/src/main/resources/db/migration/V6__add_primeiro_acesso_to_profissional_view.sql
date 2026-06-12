CREATE OR REPLACE VIEW atendimento.vw_profissional_saude AS
SELECT pds.id AS profissional_saude_id,
       u.nome_completo AS nome,
       u.email,
       u.senha,
       u.cargo AS perfil,
       u.contato,
       pds.ativo AS ativo,
       pds.documento_profissional AS registro_profissional,
       aa.area AS especialidade,
       u.id AS usuario_id,
       u.cpf,
       u.primeiro_acesso
FROM apae_geral.usuarios u
         INNER JOIN apae_geral.profissionais_da_saude pds
                    ON pds.usuario_id = u.id
         INNER JOIN apae_geral.areas_de_atendimento aa
                    ON aa.area = pds.area_de_atendimento;