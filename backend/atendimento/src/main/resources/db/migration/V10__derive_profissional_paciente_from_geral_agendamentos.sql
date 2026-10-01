-- Libera o nome da relação sem perder os dados antes da criação da VIEW.
ALTER TABLE atendimento.profissional_paciente
    RENAME TO profissional_paciente_legado;

-- A primeira ocorrência de uma agenda do Geral estabelece o vínculo.
-- Mantemos agendas inativas no histórico: desativar uma recorrência não revoga o vínculo.
CREATE VIEW atendimento.profissional_paciente AS
SELECT DISTINCT a.profissional_id, ca.paciente_id
FROM apae_geral.agendamentos a
JOIN apae_geral.cadastros_anuais ca ON ca.id = a.cadastro_anual_id;

COMMENT ON VIEW atendimento.profissional_paciente IS
    'Vínculos de leitura derivados do histórico de agendamentos do apae_geral; não inserir diretamente.';

DROP TABLE atendimento.profissional_paciente_legado;
