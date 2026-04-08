package br.org.apae.atendimento.mappers;
import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.utils.StringSanitizer;

import org.springframework.stereotype.Component;

import br.org.apae.atendimento.entities.Paciente;

@Component
public class ArquivoMapper extends AbstractMapper<Arquivo, ArquivoRequestDTO, ArquivoResponseDTO> {

    private final PacienteRepository pacienteRepository;

    public ArquivoMapper(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Arquivo toEntityPadrao(ArquivoRequestDTO dto) {
        Arquivo arquivo = new Arquivo();

        Paciente paciente = pacienteRepository.getReferenceById(dto.pacienteId());
        arquivo.setPaciente(paciente);

        TipoArquivo tipoArquivo = new TipoArquivo();
        tipoArquivo.setId(dto.tipoArquivo());
        arquivo.setTipo(tipoArquivo);

        String tituloLimpo = StringSanitizer.normalize(dto.titulo());
        arquivo.setTitulo(StringSanitizer.sanitize(tituloLimpo));
        arquivo.setTituloCanonical(StringSanitizer.canonicalize(dto.titulo()));

        String descLimpa = StringSanitizer.sanitize(StringSanitizer.normalize(dto.descricao()));
        arquivo.setDescricao(descLimpa);

        arquivo.setData(dto.data());

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
