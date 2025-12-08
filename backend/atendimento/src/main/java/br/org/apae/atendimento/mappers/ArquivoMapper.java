package br.org.apae.atendimento.mappers;
import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.TipoArquivo;
import org.springframework.stereotype.Component;

import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;

@Component
public class ArquivoMapper extends AbstractMapper<Arquivo, ArquivoRequestDTO, ArquivoResponseDTO> {
    @Override
    public Arquivo toEntityPadrao(ArquivoRequestDTO dtoPadraoArquivo) {
        ProfissionalSaude profissionalSaude = new ProfissionalSaude();
        profissionalSaude.setId(dtoPadraoArquivo.profissionalId());

        Paciente paciente = new Paciente();
        paciente.setId(dtoPadraoArquivo.pacienteId());

        TipoArquivo tipoArquivo = new TipoArquivo();
        tipoArquivo.setId(dtoPadraoArquivo.tipoArquivo());

        Arquivo arquivo = new Arquivo();
        arquivo.setData(dtoPadraoArquivo.data());
        arquivo.setPaciente(paciente);
        arquivo.setProfissional(profissionalSaude);
        arquivo.setTitulo(dtoPadraoArquivo.titulo());
        arquivo.setDescricao(dtoPadraoArquivo.descricao());
        arquivo.setTipo(tipoArquivo);
        return arquivo;
    }

    @Override
    public ArquivoResponseDTO toDTOPadrao(Arquivo arquivo) {
        return new ArquivoResponseDTO(
                arquivo.getObjectName(),
                arquivo.getPresignedUrl(),
                arquivo.getData(),
                arquivo.getNomeArquivo(),
                arquivo.getTitulo(),
                arquivo.getDescricao()
        );
    }
}
