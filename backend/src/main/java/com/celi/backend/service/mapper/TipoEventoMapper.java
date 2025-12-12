package com.celi.backend.service.mapper;

import com.celi.backend.domain.TipoEvento;
import com.celi.backend.service.dto.TipoEventoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TipoEvento} and its DTO {@link TipoEventoDTO}.
 */
@Mapper(componentModel = "spring")
public interface TipoEventoMapper extends EntityMapper<TipoEventoDTO, TipoEvento> {}
