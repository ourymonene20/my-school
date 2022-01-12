package sn.isi.myschool.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.isi.myschool.domain.Tuteurs;

/**
 * Service Interface for managing {@link Tuteurs}.
 */
public interface TuteursService {
    /**
     * Save a tuteurs.
     *
     * @param tuteurs the entity to save.
     * @return the persisted entity.
     */
    Tuteurs save(Tuteurs tuteurs);

    /**
     * Partially updates a tuteurs.
     *
     * @param tuteurs the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Tuteurs> partialUpdate(Tuteurs tuteurs);

    /**
     * Get all the tuteurs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Tuteurs> findAll(Pageable pageable);

    /**
     * Get the "id" tuteurs.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Tuteurs> findOne(Long id);

    /**
     * Delete the "id" tuteurs.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
