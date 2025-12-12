package com.celi.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.celi.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TipoEventoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoEventoDTO.class);
        TipoEventoDTO tipoEventoDTO1 = new TipoEventoDTO();
        tipoEventoDTO1.setId(1L);
        TipoEventoDTO tipoEventoDTO2 = new TipoEventoDTO();
        assertThat(tipoEventoDTO1).isNotEqualTo(tipoEventoDTO2);
        tipoEventoDTO2.setId(tipoEventoDTO1.getId());
        assertThat(tipoEventoDTO1).isEqualTo(tipoEventoDTO2);
        tipoEventoDTO2.setId(2L);
        assertThat(tipoEventoDTO1).isNotEqualTo(tipoEventoDTO2);
        tipoEventoDTO1.setId(null);
        assertThat(tipoEventoDTO1).isNotEqualTo(tipoEventoDTO2);
    }
}
