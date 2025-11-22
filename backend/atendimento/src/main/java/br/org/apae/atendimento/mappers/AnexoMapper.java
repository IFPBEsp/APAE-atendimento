package br.org.apae.atendimento.mappers;
import br.org.apae.atendimento.dtos.ArquivoDTO;
import br.org.apae.atendimento.entities.Anexo;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;


public class AnexoMapper extends AbstractMapper<Anexo, ArquivoDTO, ArquivoDTO>{
    @Override
    public Anexo toEntityPadrao(ArquivoDTO dtoPadraoAnexo) {
        ProfissionalSaude profissionalSaude = new ProfissionalSaude();
        profissionalSaude.setId(dtoPadraoAnexo.profissionalId());

        Paciente paciente = new Paciente();
        paciente.setId(dtoPadraoAnexo.pacienteId());

        Anexo anexo = new Anexo();
        anexo.setObjectName(dtoPadraoAnexo.objectName());
        anexo.setBucket(dtoPadraoAnexo.bucket());
        anexo.setPresignedUrl(dtoPadraoAnexo.presignedUrl());
        anexo.setNomeAnexo(dtoPadraoAnexo.nomeAnexo());
        anexo.setData(dtoPadraoAnexo.data());
        anexo.setPaciente(paciente);
        anexo.setProfissional(profissionalSaude);
        return anexo;
    }

    @Override
    public ArquivoDTO toDTOPadrao(Anexo anexo) {
        return new ArquivoDTO(
                anexo.getObjectName(),
                anexo.getBucket(),
                anexo.getPresignedUrl(),
                anexo.getNomeAnexo(),
                anexo.getData(),
                anexo.getPaciente().getId(),
                anexo.getProfissional().getId()
        );
    }
}
