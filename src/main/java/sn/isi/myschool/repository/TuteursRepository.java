package sn.isi.myschool.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.myschool.domain.Tuteurs;

/**
 * Spring Data SQL repository for the Tuteurs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TuteursRepository extends JpaRepository<Tuteurs, Long> {}
