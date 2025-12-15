package com.celi.backend.service.mapper;

import com.celi.backend.domain.Evento;
import com.celi.backend.domain.User;
import com.celi.backend.domain.Venta;
import com.celi.backend.service.dto.EventoDTO;
import com.celi.backend.service.dto.UserDTO;
import com.celi.backend.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Venta} and its DTO {@link VentaDTO}.
 */
@Mapper(componentModel = "spring", uses = { IntegranteMapper.class, EventoMapper.class })
public interface VentaMapper extends EntityMapper<VentaDTO, Venta> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoTitulo")
    VentaDTO toDto(Venta s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("eventoTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    EventoDTO toDtoEventoTitulo(Evento evento);
}
