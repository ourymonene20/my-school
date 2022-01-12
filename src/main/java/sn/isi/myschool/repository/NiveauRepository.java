package sn.isi.myschool.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.myschool.domain.Niveau;

/**
 * Spring Data SQL repository for the Niveau entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NiveauRepository extends JpaRepository<Niveau, Long> {}
