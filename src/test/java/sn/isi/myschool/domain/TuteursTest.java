package sn.isi.myschool.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.myschool.web.rest.TestUtil;

class TuteursTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tuteurs.class);
        Tuteurs tuteurs1 = new Tuteurs();
        tuteurs1.setId(1L);
        Tuteurs tuteurs2 = new Tuteurs();
        tuteurs2.setId(tuteurs1.getId());
        assertThat(tuteurs1).isEqualTo(tuteurs2);
        tuteurs2.setId(2L);
        assertThat(tuteurs1).isNotEqualTo(tuteurs2);
        tuteurs1.setId(null);
        assertThat(tuteurs1).isNotEqualTo(tuteurs2);
    }
}
