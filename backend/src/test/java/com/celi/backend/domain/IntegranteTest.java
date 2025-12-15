package com.celi.backend.domain;

import static com.celi.backend.domain.EventoTestSamples.*;
import static com.celi.backend.domain.IntegranteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.celi.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntegranteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Integrante.class);
        Integrante integrante1 = getIntegranteSample1();
        Integrante integrante2 = new Integrante();
        assertThat(integrante1).isNotEqualTo(integrante2);

        integrante2.setId(integrante1.getId());
        assertThat(integrante1).isEqualTo(integrante2);

        integrante2 = getIntegranteSample2();
        assertThat(integrante1).isNotEqualTo(integrante2);
    }

}
