package sn.isi.myschool.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.isi.myschool.domain.Niveau;

/**
 * Service Interface for managing {@link Niveau}.
 */
public interface NiveauService {
    /**
     * Save a niveau.
     *
     * @param niveau the entity to save.
     * @return the persisted entity.
     */
    Niveau save(Niveau niveau);

    /**
     * Partially updates a niveau.
     *
     * @param niveau the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Niveau> partialUpdate(Niveau niveau);

    /**
     * Get all the niveaus.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Niveau> findAll(Pageable pageable);

    /**
     * Get the "id" niveau.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Niveau> findOne(Long id);

    /**
     * Delete the "id" niveau.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
