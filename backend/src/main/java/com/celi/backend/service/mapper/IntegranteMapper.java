package com.celi.backend.service.mapper;

import com.celi.backend.domain.Integrante;
import com.celi.backend.service.dto.IntegranteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Integrante} and its DTO {@link IntegranteDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegranteMapper extends EntityMapper<IntegranteDTO, Integrante> {
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Integrante entity, IntegranteDTO dto);
}
