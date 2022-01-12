package sn.isi.myschool.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.myschool.domain.Absence;

/**
 * Spring Data SQL repository for the Absence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {}
