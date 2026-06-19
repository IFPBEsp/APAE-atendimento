CREATE OR REPLACE VIEW atendimento.vw_todos_pacientes AS
SELECT
    p.id AS paciente_id,
    p.nome_completo AS nome,
    p.data_de_nascimento AS data_nascimento,
    p.cpf,
    p.contato,
    e.cidade,
    e.rua,
    e.bairro,
    e.numero AS numero_casa
FROM apae_geral.pacientes p
INNER JOIN apae_geral.enderecos e
    ON e.id = p.endereco_id
WHERE p.is_apagado = false;