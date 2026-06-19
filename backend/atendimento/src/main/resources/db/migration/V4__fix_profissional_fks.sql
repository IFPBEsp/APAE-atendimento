ALTER TABLE atendimento.profissional_paciente
DROP CONSTRAINT IF EXISTS fk_pp_profissional;

ALTER TABLE atendimento.profissional_paciente
    ADD CONSTRAINT fk_pp_profissional
        FOREIGN KEY (profissional_id)
            REFERENCES apae_geral.profissionais_da_saude(id);

ALTER TABLE atendimento.atendimento
DROP CONSTRAINT IF EXISTS fk_atend_profissional;

ALTER TABLE atendimento.atendimento
    ADD CONSTRAINT fk_atend_profissional
        FOREIGN KEY (profissional_id)
            REFERENCES apae_geral.profissionais_da_saude(id);

ALTER TABLE atendimento.agendamento
DROP CONSTRAINT IF EXISTS fk_agend_profissional;

ALTER TABLE atendimento.agendamento
    ADD CONSTRAINT fk_agend_profissional
        FOREIGN KEY (profissional_id)
            REFERENCES apae_geral.profissionais_da_saude(id);

ALTER TABLE atendimento.anexo
DROP CONSTRAINT IF EXISTS fk_anexo_prof;

ALTER TABLE atendimento.anexo
    ADD CONSTRAINT fk_anexo_prof
        FOREIGN KEY (profissional_id)
            REFERENCES apae_geral.profissionais_da_saude(id);