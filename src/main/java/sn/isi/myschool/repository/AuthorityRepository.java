package sn.isi.myschool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.isi.myschool.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
