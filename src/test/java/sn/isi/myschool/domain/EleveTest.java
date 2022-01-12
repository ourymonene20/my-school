package sn.isi.myschool.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.myschool.web.rest.TestUtil;

class EleveTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Eleve.class);
        Eleve eleve1 = new Eleve();
        eleve1.setId(1L);
        Eleve eleve2 = new Eleve();
        eleve2.setId(eleve1.getId());
        assertThat(eleve1).isEqualTo(eleve2);
        eleve2.setId(2L);
        assertThat(eleve1).isNotEqualTo(eleve2);
        eleve1.setId(null);
        assertThat(eleve1).isNotEqualTo(eleve2);
    }
}
