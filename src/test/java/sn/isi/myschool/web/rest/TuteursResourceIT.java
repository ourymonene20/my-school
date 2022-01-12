package sn.isi.myschool.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import sn.isi.myschool.domain.Tuteurs;
import sn.isi.myschool.repository.TuteursRepository;

/**
 * Integration tests for the {@link TuteursResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TuteursResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tuteurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TuteursRepository tuteursRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTuteursMockMvc;

    private Tuteurs tuteurs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tuteurs createEntity(EntityManager em) {
        Tuteurs tuteurs = new Tuteurs().nom(DEFAULT_NOM).prenom(DEFAULT_PRENOM).telephone(DEFAULT_TELEPHONE).email(DEFAULT_EMAIL);
        return tuteurs;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tuteurs createUpdatedEntity(EntityManager em) {
        Tuteurs tuteurs = new Tuteurs().nom(UPDATED_NOM).prenom(UPDATED_PRENOM).telephone(UPDATED_TELEPHONE).email(UPDATED_EMAIL);
        return tuteurs;
    }

    @BeforeEach
    public void initTest() {
        tuteurs = createEntity(em);
    }

    @Test
    @Transactional
    void createTuteurs() throws Exception {
        int databaseSizeBeforeCreate = tuteursRepository.findAll().size();
        // Create the Tuteurs
        restTuteursMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isCreated());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeCreate + 1);
        Tuteurs testTuteurs = tuteursList.get(tuteursList.size() - 1);
        assertThat(testTuteurs.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testTuteurs.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testTuteurs.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testTuteurs.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void createTuteursWithExistingId() throws Exception {
        // Create the Tuteurs with an existing ID
        tuteurs.setId(1L);

        int databaseSizeBeforeCreate = tuteursRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTuteursMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTelephoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = tuteursRepository.findAll().size();
        // set the field null
        tuteurs.setTelephone(null);

        // Create the Tuteurs, which fails.

        restTuteursMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isBadRequest());

        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTuteurs() throws Exception {
        // Initialize the database
        tuteursRepository.saveAndFlush(tuteurs);

        // Get all the tuteursList
        restTuteursMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tuteurs.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    @Transactional
    void getTuteurs() throws Exception {
        // Initialize the database
        tuteursRepository.saveAndFlush(tuteurs);

        // Get the tuteurs
        restTuteursMockMvc
            .perform(get(ENTITY_API_URL_ID, tuteurs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tuteurs.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getNonExistingTuteurs() throws Exception {
        // Get the tuteurs
        restTuteursMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTuteurs() throws Exception {
        // Initialize the database
        tuteursRepository.saveAndFlush(tuteurs);

        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();

        // Update the tuteurs
        Tuteurs updatedTuteurs = tuteursRepository.findById(tuteurs.getId()).get();
        // Disconnect from session so that the updates on updatedTuteurs are not directly saved in db
        em.detach(updatedTuteurs);
        updatedTuteurs.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).telephone(UPDATED_TELEPHONE).email(UPDATED_EMAIL);

        restTuteursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTuteurs.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTuteurs))
            )
            .andExpect(status().isOk());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
        Tuteurs testTuteurs = tuteursList.get(tuteursList.size() - 1);
        assertThat(testTuteurs.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTuteurs.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testTuteurs.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testTuteurs.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void putNonExistingTuteurs() throws Exception {
        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();
        tuteurs.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTuteursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tuteurs.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTuteurs() throws Exception {
        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();
        tuteurs.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTuteursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTuteurs() throws Exception {
        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();
        tuteurs.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTuteursMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTuteursWithPatch() throws Exception {
        // Initialize the database
        tuteursRepository.saveAndFlush(tuteurs);

        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();

        // Update the tuteurs using partial update
        Tuteurs partialUpdatedTuteurs = new Tuteurs();
        partialUpdatedTuteurs.setId(tuteurs.getId());

        partialUpdatedTuteurs.nom(UPDATED_NOM).email(UPDATED_EMAIL);

        restTuteursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTuteurs.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTuteurs))
            )
            .andExpect(status().isOk());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
        Tuteurs testTuteurs = tuteursList.get(tuteursList.size() - 1);
        assertThat(testTuteurs.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTuteurs.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testTuteurs.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testTuteurs.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void fullUpdateTuteursWithPatch() throws Exception {
        // Initialize the database
        tuteursRepository.saveAndFlush(tuteurs);

        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();

        // Update the tuteurs using partial update
        Tuteurs partialUpdatedTuteurs = new Tuteurs();
        partialUpdatedTuteurs.setId(tuteurs.getId());

        partialUpdatedTuteurs.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).telephone(UPDATED_TELEPHONE).email(UPDATED_EMAIL);

        restTuteursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTuteurs.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTuteurs))
            )
            .andExpect(status().isOk());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
        Tuteurs testTuteurs = tuteursList.get(tuteursList.size() - 1);
        assertThat(testTuteurs.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTuteurs.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testTuteurs.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testTuteurs.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void patchNonExistingTuteurs() throws Exception {
        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();
        tuteurs.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTuteursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tuteurs.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTuteurs() throws Exception {
        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();
        tuteurs.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTuteursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTuteurs() throws Exception {
        int databaseSizeBeforeUpdate = tuteursRepository.findAll().size();
        tuteurs.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTuteursMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tuteurs))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tuteurs in the database
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTuteurs() throws Exception {
        // Initialize the database
        tuteursRepository.saveAndFlush(tuteurs);

        int databaseSizeBeforeDelete = tuteursRepository.findAll().size();

        // Delete the tuteurs
        restTuteursMockMvc
            .perform(delete(ENTITY_API_URL_ID, tuteurs.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tuteurs> tuteursList = tuteursRepository.findAll();
        assertThat(tuteursList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
