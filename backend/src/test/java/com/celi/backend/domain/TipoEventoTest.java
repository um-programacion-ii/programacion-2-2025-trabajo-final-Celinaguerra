package com.celi.backend.domain;

import static com.celi.backend.domain.TipoEventoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.celi.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TipoEventoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoEvento.class);
        TipoEvento tipoEvento1 = getTipoEventoSample1();
        TipoEvento tipoEvento2 = new TipoEvento();
        assertThat(tipoEvento1).isNotEqualTo(tipoEvento2);

        tipoEvento2.setId(tipoEvento1.getId());
        assertThat(tipoEvento1).isEqualTo(tipoEvento2);

        tipoEvento2 = getTipoEventoSample2();
        assertThat(tipoEvento1).isNotEqualTo(tipoEvento2);
    }
}
