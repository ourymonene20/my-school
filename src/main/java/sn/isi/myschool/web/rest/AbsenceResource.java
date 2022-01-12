package sn.isi.myschool.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.isi.myschool.domain.Absence;
import sn.isi.myschool.repository.AbsenceRepository;
import sn.isi.myschool.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.myschool.domain.Absence}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AbsenceResource {

    private final Logger log = LoggerFactory.getLogger(AbsenceResource.class);

    private static final String ENTITY_NAME = "absence";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AbsenceRepository absenceRepository;

    public AbsenceResource(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    /**
     * {@code POST  /absences} : Create a new absence.
     *
     * @param absence the absence to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new absence, or with status {@code 400 (Bad Request)} if the absence has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/absences")
    public ResponseEntity<Absence> createAbsence(@RequestBody Absence absence) throws URISyntaxException {
        log.debug("REST request to save Absence : {}", absence);
        if (absence.getId() != null) {
            throw new BadRequestAlertException("A new absence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Absence result = absenceRepository.save(absence);
        return ResponseEntity
            .created(new URI("/api/absences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /absences/:id} : Updates an existing absence.
     *
     * @param id the id of the absence to save.
     * @param absence the absence to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated absence,
     * or with status {@code 400 (Bad Request)} if the absence is not valid,
     * or with status {@code 500 (Internal Server Error)} if the absence couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/absences/{id}")
    public ResponseEntity<Absence> updateAbsence(@PathVariable(value = "id", required = false) final Long id, @RequestBody Absence absence)
        throws URISyntaxException {
        log.debug("REST request to update Absence : {}, {}", id, absence);
        if (absence.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, absence.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!absenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Absence result = absenceRepository.save(absence);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, absence.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /absences/:id} : Partial updates given fields of an existing absence, field will ignore if it is null
     *
     * @param id the id of the absence to save.
     * @param absence the absence to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated absence,
     * or with status {@code 400 (Bad Request)} if the absence is not valid,
     * or with status {@code 404 (Not Found)} if the absence is not found,
     * or with status {@code 500 (Internal Server Error)} if the absence couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/absences/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Absence> partialUpdateAbsence(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Absence absence
    ) throws URISyntaxException {
        log.debug("REST request to partial update Absence partially : {}, {}", id, absence);
        if (absence.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, absence.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!absenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Absence> result = absenceRepository
            .findById(absence.getId())
            .map(existingAbsence -> {
                if (absence.getDateAbsence() != null) {
                    existingAbsence.setDateAbsence(absence.getDateAbsence());
                }

                return existingAbsence;
            })
            .map(absenceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, absence.getId().toString())
        );
    }

    /**
     * {@code GET  /absences} : get all the absences.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of absences in body.
     */
    @GetMapping("/absences")
    public ResponseEntity<List<Absence>> getAllAbsences(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Absences");
        Page<Absence> page = absenceRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /absences/:id} : get the "id" absence.
     *
     * @param id the id of the absence to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the absence, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/absences/{id}")
    public ResponseEntity<Absence> getAbsence(@PathVariable Long id) {
        log.debug("REST request to get Absence : {}", id);
        Optional<Absence> absence = absenceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(absence);
    }

    /**
     * {@code DELETE  /absences/:id} : delete the "id" absence.
     *
     * @param id the id of the absence to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/absences/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        log.debug("REST request to delete Absence : {}", id);
        absenceRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
