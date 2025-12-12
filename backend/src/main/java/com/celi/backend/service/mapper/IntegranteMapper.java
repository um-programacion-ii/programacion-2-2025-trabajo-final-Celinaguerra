package com.celi.backend.service.mapper;

import com.celi.backend.domain.Evento;
import com.celi.backend.domain.Integrante;
import com.celi.backend.service.dto.EventoDTO;
import com.celi.backend.service.dto.IntegranteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Integrante} and its DTO {@link IntegranteDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegranteMapper extends EntityMapper<IntegranteDTO, Integrante> {
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoId")
    IntegranteDTO toDto(Integrante s);

    @Named("eventoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventoDTO toDtoEventoId(Evento evento);
}
