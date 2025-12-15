package com.celi.backend.service.mapper;

import com.celi.backend.domain.Asiento;
import com.celi.backend.domain.Venta;
import com.celi.backend.service.dto.AsientoDTO;
import com.celi.backend.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Asiento} and its DTO {@link AsientoDTO}.
 */
@Mapper(componentModel = "spring", uses = { VentaMapper.class, IntegranteMapper.class })
public interface AsientoMapper extends EntityMapper<AsientoDTO, Asiento> {
    @Mapping(target = "venta", source = "venta", qualifiedByName = "ventaId")
    AsientoDTO toDto(Asiento s);

    @Named("ventaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VentaDTO toDtoVentaId(Venta venta);
}
