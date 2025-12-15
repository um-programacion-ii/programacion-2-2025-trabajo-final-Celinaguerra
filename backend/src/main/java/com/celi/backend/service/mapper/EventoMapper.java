package com.celi.backend.service.mapper;

import com.celi.backend.domain.Evento;
import com.celi.backend.domain.TipoEvento;
import com.celi.backend.service.dto.EventoDTO;
import com.celi.backend.service.dto.TipoEventoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Evento} and its DTO {@link EventoDTO}.
 */
@Mapper(componentModel = "spring", uses = { IntegranteMapper.class })
public interface EventoMapper extends EntityMapper<EventoDTO, Evento> {
    @Mapping(target = "eventoTipo", source = "eventoTipo", qualifiedByName = "tipoEventoNombre")
    EventoDTO toDto(Evento s);

    @Named("tipoEventoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "descripcion", source = "descripcion")
    TipoEventoDTO toDtoTipoEventoNombre(TipoEvento tipoEvento);
}
