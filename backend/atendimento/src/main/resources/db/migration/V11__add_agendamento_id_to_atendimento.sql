ALTER TABLE atendimento.atendimento
    ADD COLUMN IF NOT EXISTS agendamento_id UUID NULL;

ALTER TABLE atendimento.atendimento
    ADD CONSTRAINT fk_atendimento_agendamento
    FOREIGN KEY (agendamento_id)
    REFERENCES atendimento.agendamento (id);
    