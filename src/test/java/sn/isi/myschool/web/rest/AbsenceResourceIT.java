package sn.isi.myschool.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.myschool.IntegrationTest;
import sn.isi.myschool.domain.Absence;
import sn.isi.myschool.repository.AbsenceRepository;

/**
 * Integration tests for the {@link AbsenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AbsenceResourceIT {

    private static final Instant DEFAULT_DATE_ABSENCE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ABSENCE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/absences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAbsenceMockMvc;

    private Absence absence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Absence createEntity(EntityManager em) {
        Absence absence = new Absence().dateAbsence(DEFAULT_DATE_ABSENCE);
        return absence;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Absence createUpdatedEntity(EntityManager em) {
        Absence absence = new Absence().dateAbsence(UPDATED_DATE_ABSENCE);
        return absence;
    }

    @BeforeEach
    public void initTest() {
        absence = createEntity(em);
    }

    @Test
    @Transactional
    void createAbsence() throws Exception {
        int databaseSizeBeforeCreate = absenceRepository.findAll().size();
        // Create the Absence
        restAbsenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(absence))
            )
            .andExpect(status().isCreated());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeCreate + 1);
        Absence testAbsence = absenceList.get(absenceList.size() - 1);
        assertThat(testAbsence.getDateAbsence()).isEqualTo(DEFAULT_DATE_ABSENCE);
    }

    @Test
    @Transactional
    void createAbsenceWithExistingId() throws Exception {
        // Create the Absence with an existing ID
        absence.setId(1L);

        int databaseSizeBeforeCreate = absenceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAbsenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(absence))
            )
            .andExpect(status().isBadRequest());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAbsences() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);

        // Get all the absenceList
        restAbsenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(absence.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAbsence").value(hasItem(DEFAULT_DATE_ABSENCE.toString())));
    }

    @Test
    @Transactional
    void getAbsence() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);

        // Get the absence
        restAbsenceMockMvc
            .perform(get(ENTITY_API_URL_ID, absence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(absence.getId().intValue()))
            .andExpect(jsonPath("$.dateAbsence").value(DEFAULT_DATE_ABSENCE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAbsence() throws Exception {
        // Get the absence
        restAbsenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAbsence() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);

        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();

        // Update the absence
        Absence updatedAbsence = absenceRepository.findById(absence.getId()).get();
        // Disconnect from session so that the updates on updatedAbsence are not directly saved in db
        em.detach(updatedAbsence);
        updatedAbsence.dateAbsence(UPDATED_DATE_ABSENCE);

        restAbsenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAbsence.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAbsence))
            )
            .andExpect(status().isOk());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
        Absence testAbsence = absenceList.get(absenceList.size() - 1);
        assertThat(testAbsence.getDateAbsence()).isEqualTo(UPDATED_DATE_ABSENCE);
    }

    @Test
    @Transactional
    void putNonExistingAbsence() throws Exception {
        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();
        absence.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAbsenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, absence.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(absence))
            )
            .andExpect(status().isBadRequest());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAbsence() throws Exception {
        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();
        absence.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbsenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(absence))
            )
            .andExpect(status().isBadRequest());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAbsence() throws Exception {
        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();
        absence.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbsenceMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(absence))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAbsenceWithPatch() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);

        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();

        // Update the absence using partial update
        Absence partialUpdatedAbsence = new Absence();
        partialUpdatedAbsence.setId(absence.getId());

        partialUpdatedAbsence.dateAbsence(UPDATED_DATE_ABSENCE);

        restAbsenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAbsence.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAbsence))
            )
            .andExpect(status().isOk());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
        Absence testAbsence = absenceList.get(absenceList.size() - 1);
        assertThat(testAbsence.getDateAbsence()).isEqualTo(UPDATED_DATE_ABSENCE);
    }

    @Test
    @Transactional
    void fullUpdateAbsenceWithPatch() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);

        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();

        // Update the absence using partial update
        Absence partialUpdatedAbsence = new Absence();
        partialUpdatedAbsence.setId(absence.getId());

        partialUpdatedAbsence.dateAbsence(UPDATED_DATE_ABSENCE);

        restAbsenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAbsence.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAbsence))
            )
            .andExpect(status().isOk());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
        Absence testAbsence = absenceList.get(absenceList.size() - 1);
        assertThat(testAbsence.getDateAbsence()).isEqualTo(UPDATED_DATE_ABSENCE);
    }

    @Test
    @Transactional
    void patchNonExistingAbsence() throws Exception {
        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();
        absence.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAbsenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, absence.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(absence))
            )
            .andExpect(status().isBadRequest());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAbsence() throws Exception {
        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();
        absence.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbsenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(absence))
            )
            .andExpect(status().isBadRequest());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAbsence() throws Exception {
        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();
        absence.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbsenceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(absence))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Absence in the database
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAbsence() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);

        int databaseSizeBeforeDelete = absenceRepository.findAll().size();

        // Delete the absence
        restAbsenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, absence.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Absence> absenceList = absenceRepository.findAll();
        assertThat(absenceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
