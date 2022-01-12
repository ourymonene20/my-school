package sn.isi.myschool.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.myschool.web.rest.TestUtil;

class AbsenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Absence.class);
        Absence absence1 = new Absence();
        absence1.setId(1L);
        Absence absence2 = new Absence();
        absence2.setId(absence1.getId());
        assertThat(absence1).isEqualTo(absence2);
        absence2.setId(2L);
        assertThat(absence1).isNotEqualTo(absence2);
        absence1.setId(null);
        assertThat(absence1).isNotEqualTo(absence2);
    }
}
