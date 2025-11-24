package br.org.apae.atendimento.mappers;
import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import org.springframework.stereotype.Component;

import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;

@Component
public class AnexoMapper extends AbstractMapper<Arquivo, ArquivoRequestDTO, ArquivoResponseDTO> {
    @Override
    public Arquivo toEntityPadrao(ArquivoRequestDTO dtoPadraoArquivo) {
        ProfissionalSaude profissionalSaude = new ProfissionalSaude();
        profissionalSaude.setId(dtoPadraoArquivo.profissionalId());

        Paciente paciente = new Paciente();
        paciente.setId(dtoPadraoArquivo.pacienteId());

        Arquivo anexo = new Arquivo();
        anexo.setData(dtoPadraoArquivo.data());
        anexo.setPaciente(paciente);
        anexo.setProfissional(profissionalSaude);
        return anexo;
    }

    @Override
    public ArquivoResponseDTO toDTOPadrao(Arquivo arquivo) {
        return new ArquivoResponseDTO(
                arquivo.getPresignedUrl(),
                arquivo.getData(),
                arquivo.getNomeArquivo()
        );
    }
}
