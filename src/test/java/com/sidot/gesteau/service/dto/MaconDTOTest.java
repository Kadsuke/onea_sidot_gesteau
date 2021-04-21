package com.sidot.gesteau.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sidot.gesteau.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaconDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MaconDTO.class);
        MaconDTO maconDTO1 = new MaconDTO();
        maconDTO1.setId(1L);
        MaconDTO maconDTO2 = new MaconDTO();
        assertThat(maconDTO1).isNotEqualTo(maconDTO2);
        maconDTO2.setId(maconDTO1.getId());
        assertThat(maconDTO1).isEqualTo(maconDTO2);
        maconDTO2.setId(2L);
        assertThat(maconDTO1).isNotEqualTo(maconDTO2);
        maconDTO1.setId(null);
        assertThat(maconDTO1).isNotEqualTo(maconDTO2);
    }
}
