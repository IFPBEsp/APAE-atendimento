package br.org.apae.atendimento.mappers;

public interface IMapper<Entity, DTORequest, DTOResponse>{
    Entity toEntityPadrao(DTORequest dtoPadrao);
    DTOResponse toDTOPadrao(Entity entidadePadrao);
}
