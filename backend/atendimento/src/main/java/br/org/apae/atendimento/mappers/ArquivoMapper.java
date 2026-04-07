package br.org.apae.atendimento.mappers;
import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.repositories.PacienteRepository;
import org.springframework.stereotype.Component;

import br.org.apae.atendimento.entities.Paciente;

@Component
public class ArquivoMapper extends AbstractMapper<Arquivo, ArquivoRequestDTO, ArquivoResponseDTO> {

    private final PacienteRepository pacienteRepository;

    public ArquivoMapper(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Arquivo toEntityPadrao(ArquivoRequestDTO dtoPadraoArquivo) {
        Arquivo arquivo = new Arquivo();

        Paciente paciente = pacienteRepository.getReferenceById(dtoPadraoArquivo.pacienteId());
        arquivo.setPaciente(paciente);

        TipoArquivo tipoArquivo = new TipoArquivo();
        tipoArquivo.setId(dtoPadraoArquivo.tipoArquivo());
        arquivo.setTipo(tipoArquivo);

        arquivo.setData(dtoPadraoArquivo.data());
        arquivo.setTitulo(dtoPadraoArquivo.titulo());
        arquivo.setDescricao(dtoPadraoArquivo.descricao());
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
