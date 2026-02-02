package br.org.apae.atendimento.mappers;

public abstract class AbstractMapper<Entity, DTORequest, DTOResponse> implements IMapper<Entity, DTORequest, DTOResponse> {
    @Override
    public abstract Entity toEntityPadrao(DTORequest dtoPadrao);

    @Override
    public abstract DTOResponse toDTOPadrao(Entity entidadePadrao);
}
