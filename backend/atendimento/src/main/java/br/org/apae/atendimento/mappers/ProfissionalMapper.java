package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import org.springframework.stereotype.Component;

import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.entities.views.ProfissionalSaude;

@Component
public class ProfissionalMapper extends AbstractMapper<ProfissionalSaude, Void, ProfissionalResponseDTO>{
    
    @Override
    public ProfissionalSaude toEntityPadrao(Void dtoPadraoProfissionalSaude) {
        return null;
    }

    @Override
    public ProfissionalResponseDTO toDTOPadrao(ProfissionalSaude profissional) {

        String primeiroNome = extrairPrimeiroNome(profissional.getNomeCompleto());


        return new ProfissionalResponseDTO(
                profissional.getId(),
                primeiroNome,
                profissional.getNomeCompleto(),
                profissional.getEmail(),
                profissional.getContato()
        );
    }

    private String extrairPrimeiroNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) return "Doutor(a)";
        return nomeCompleto.trim().split("\\s+")[0];
    }

}
