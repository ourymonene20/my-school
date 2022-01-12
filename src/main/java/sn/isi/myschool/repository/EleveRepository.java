package sn.isi.myschool.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.myschool.domain.Eleve;

/**
 * Spring Data SQL repository for the Eleve entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EleveRepository extends JpaRepository<Eleve, Long> {}
