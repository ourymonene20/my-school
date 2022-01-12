package sn.isi.myschool.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.myschool.web.rest.TestUtil;

class SalleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Salle.class);
        Salle salle1 = new Salle();
        salle1.setId(1L);
        Salle salle2 = new Salle();
        salle2.setId(salle1.getId());
        assertThat(salle1).isEqualTo(salle2);
        salle2.setId(2L);
        assertThat(salle1).isNotEqualTo(salle2);
        salle1.setId(null);
        assertThat(salle1).isNotEqualTo(salle2);
    }
}
