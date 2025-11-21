package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.entities.ProfissionalSaude;

public class ProfissionalMapper extends AbstractMapper<ProfissionalSaude, Void, ProfissionalResponseDTO>{
    
    @Override
    public ProfissionalSaude toEntityPadrao(Void dtoPadraoProfissionalSaude) {
        return null;
    }

    @Override
    public ProfissionalResponseDTO toDTOPadrao(ProfissionalSaude profissional) {
        return new ProfissionalResponseDTO(
                profissional.getId(),
                profissional.getPrimeiroNome(),
                profissional.getNomeCompleto(),
                profissional.getEmail(),
                profissional.getContato(),
                profissional.getCrm()
        );
    }

}
