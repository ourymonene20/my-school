package sn.isi.myschool.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.isi.myschool.domain.Salle;

/**
 * Service Interface for managing {@link Salle}.
 */
public interface SalleService {
    /**
     * Save a salle.
     *
     * @param salle the entity to save.
     * @return the persisted entity.
     */
    Salle save(Salle salle);

    /**
     * Partially updates a salle.
     *
     * @param salle the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Salle> partialUpdate(Salle salle);

    /**
     * Get all the salles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Salle> findAll(Pageable pageable);

    /**
     * Get the "id" salle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Salle> findOne(Long id);

    /**
     * Delete the "id" salle.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
