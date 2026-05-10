package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;

public abstract class AbstractMapper<Entity, DTORequest, DTOResponse> implements IMapper<Entity, DTORequest, DTOResponse> {
    @Override
    public abstract Entity toEntityPadrao(DTORequest dtoPadrao);

    @Override
    public abstract DTOResponse toDTOPadrao(Entity entidadePadrao);
}
