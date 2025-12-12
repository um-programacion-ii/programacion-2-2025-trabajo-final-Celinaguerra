package com.celi.backend.service.mapper;

import static com.celi.backend.domain.EventoAsserts.*;
import static com.celi.backend.domain.EventoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventoMapperTest {

    private EventoMapper eventoMapper;

    @BeforeEach
    void setUp() {
        eventoMapper = new EventoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventoSample1();
        var actual = eventoMapper.toEntity(eventoMapper.toDto(expected));
        assertEventoAllPropertiesEquals(expected, actual);
    }
}
