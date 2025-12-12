package com.celi.backend.service.mapper;

import static com.celi.backend.domain.TipoEventoAsserts.*;
import static com.celi.backend.domain.TipoEventoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TipoEventoMapperTest {

    private TipoEventoMapper tipoEventoMapper;

    @BeforeEach
    void setUp() {
        tipoEventoMapper = new TipoEventoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTipoEventoSample1();
        var actual = tipoEventoMapper.toEntity(tipoEventoMapper.toDto(expected));
        assertTipoEventoAllPropertiesEquals(expected, actual);
    }
}
