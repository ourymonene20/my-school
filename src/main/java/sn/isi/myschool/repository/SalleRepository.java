package sn.isi.myschool.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.myschool.domain.Salle;

/**
 * Spring Data SQL repository for the Salle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {}
