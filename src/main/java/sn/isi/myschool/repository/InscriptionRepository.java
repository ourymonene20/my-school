package sn.isi.myschool.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.myschool.domain.Inscription;

/**
 * Spring Data SQL repository for the Inscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {}
