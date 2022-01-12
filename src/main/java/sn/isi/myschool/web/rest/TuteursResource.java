package sn.isi.myschool.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.isi.myschool.domain.Tuteurs;
import sn.isi.myschool.repository.TuteursRepository;
import sn.isi.myschool.service.TuteursService;
import sn.isi.myschool.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.myschool.domain.Tuteurs}.
 */
@RestController
@RequestMapping("/api")
public class TuteursResource {

    private final Logger log = LoggerFactory.getLogger(TuteursResource.class);

    private static final String ENTITY_NAME = "tuteurs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TuteursService tuteursService;

    private final TuteursRepository tuteursRepository;

    public TuteursResource(TuteursService tuteursService, TuteursRepository tuteursRepository) {
        this.tuteursService = tuteursService;
        this.tuteursRepository = tuteursRepository;
    }

    /**
     * {@code POST  /tuteurs} : Create a new tuteurs.
     *
     * @param tuteurs the tuteurs to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tuteurs, or with status {@code 400 (Bad Request)} if the tuteurs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tuteurs")
    public ResponseEntity<Tuteurs> createTuteurs(@Valid @RequestBody Tuteurs tuteurs) throws URISyntaxException {
        log.debug("REST request to save Tuteurs : {}", tuteurs);
        if (tuteurs.getId() != null) {
            throw new BadRequestAlertException("A new tuteurs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tuteurs result = tuteursService.save(tuteurs);
        return ResponseEntity
            .created(new URI("/api/tuteurs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tuteurs/:id} : Updates an existing tuteurs.
     *
     * @param id the id of the tuteurs to save.
     * @param tuteurs the tuteurs to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tuteurs,
     * or with status {@code 400 (Bad Request)} if the tuteurs is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tuteurs couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tuteurs/{id}")
    public ResponseEntity<Tuteurs> updateTuteurs(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Tuteurs tuteurs
    ) throws URISyntaxException {
        log.debug("REST request to update Tuteurs : {}, {}", id, tuteurs);
        if (tuteurs.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tuteurs.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tuteursRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Tuteurs result = tuteursService.save(tuteurs);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tuteurs.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tuteurs/:id} : Partial updates given fields of an existing tuteurs, field will ignore if it is null
     *
     * @param id the id of the tuteurs to save.
     * @param tuteurs the tuteurs to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tuteurs,
     * or with status {@code 400 (Bad Request)} if the tuteurs is not valid,
     * or with status {@code 404 (Not Found)} if the tuteurs is not found,
     * or with status {@code 500 (Internal Server Error)} if the tuteurs couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tuteurs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tuteurs> partialUpdateTuteurs(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Tuteurs tuteurs
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tuteurs partially : {}, {}", id, tuteurs);
        if (tuteurs.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tuteurs.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tuteursRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tuteurs> result = tuteursService.partialUpdate(tuteurs);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tuteurs.getId().toString())
        );
    }

    /**
     * {@code GET  /tuteurs} : get all the tuteurs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tuteurs in body.
     */
    @GetMapping("/tuteurs")
    public ResponseEntity<List<Tuteurs>> getAllTuteurs(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Tuteurs");
        Page<Tuteurs> page = tuteursService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tuteurs/:id} : get the "id" tuteurs.
     *
     * @param id the id of the tuteurs to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tuteurs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tuteurs/{id}")
    public ResponseEntity<Tuteurs> getTuteurs(@PathVariable Long id) {
        log.debug("REST request to get Tuteurs : {}", id);
        Optional<Tuteurs> tuteurs = tuteursService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tuteurs);
    }

    /**
     * {@code DELETE  /tuteurs/:id} : delete the "id" tuteurs.
     *
     * @param id the id of the tuteurs to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tuteurs/{id}")
    public ResponseEntity<Void> deleteTuteurs(@PathVariable Long id) {
        log.debug("REST request to delete Tuteurs : {}", id);
        tuteursService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
