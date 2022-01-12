package sn.isi.myschool.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.myschool.web.rest.TestUtil;

class EnseignantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Enseignant.class);
        Enseignant enseignant1 = new Enseignant();
        enseignant1.setId(1L);
        Enseignant enseignant2 = new Enseignant();
        enseignant2.setId(enseignant1.getId());
        assertThat(enseignant1).isEqualTo(enseignant2);
        enseignant2.setId(2L);
        assertThat(enseignant1).isNotEqualTo(enseignant2);
        enseignant1.setId(null);
        assertThat(enseignant1).isNotEqualTo(enseignant2);
    }
}
