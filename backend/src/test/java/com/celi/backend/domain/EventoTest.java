package com.celi.backend.domain;

import static com.celi.backend.domain.EventoTestSamples.*;
import static com.celi.backend.domain.IntegranteTestSamples.*;
import static com.celi.backend.domain.TipoEventoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.celi.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Evento.class);
        Evento evento1 = getEventoSample1();
        Evento evento2 = new Evento();
        assertThat(evento1).isNotEqualTo(evento2);

        evento2.setId(evento1.getId());
        assertThat(evento1).isEqualTo(evento2);

        evento2 = getEventoSample2();
        assertThat(evento1).isNotEqualTo(evento2);
    }

    @Test
    void integrantesTest() {
        Evento evento = getEventoRandomSampleGenerator();
        Integrante integranteBack = getIntegranteRandomSampleGenerator();

        evento.addIntegrantes(integranteBack);
        assertThat(evento.getIntegrantes()).containsOnly(integranteBack);
        assertThat(integranteBack.getEvento()).isEqualTo(evento);

        evento.removeIntegrantes(integranteBack);
        assertThat(evento.getIntegrantes()).doesNotContain(integranteBack);
        assertThat(integranteBack.getEvento()).isNull();

        evento.integrantes(new HashSet<>(Set.of(integranteBack)));
        assertThat(evento.getIntegrantes()).containsOnly(integranteBack);
        assertThat(integranteBack.getEvento()).isEqualTo(evento);

        evento.setIntegrantes(new HashSet<>());
        assertThat(evento.getIntegrantes()).doesNotContain(integranteBack);
        assertThat(integranteBack.getEvento()).isNull();
    }

    @Test
    void eventoTipoTest() {
        Evento evento = getEventoRandomSampleGenerator();
        TipoEvento tipoEventoBack = getTipoEventoRandomSampleGenerator();

        evento.setEventoTipo(tipoEventoBack);
        assertThat(evento.getEventoTipo()).isEqualTo(tipoEventoBack);

        evento.eventoTipo(null);
        assertThat(evento.getEventoTipo()).isNull();
    }
}
