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
import sn.isi.myschool.domain.Eleve;
import sn.isi.myschool.repository.EleveRepository;

/**
 * Integration tests for the {@link EleveResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EleveResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_NAISS = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_NAISS = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/eleves";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EleveRepository eleveRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEleveMockMvc;

    private Eleve eleve;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eleve createEntity(EntityManager em) {
        Eleve eleve = new Eleve().nom(DEFAULT_NOM).prenom(DEFAULT_PRENOM).email(DEFAULT_EMAIL).dateNaiss(DEFAULT_DATE_NAISS);
        return eleve;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eleve createUpdatedEntity(EntityManager em) {
        Eleve eleve = new Eleve().nom(UPDATED_NOM).prenom(UPDATED_PRENOM).email(UPDATED_EMAIL).dateNaiss(UPDATED_DATE_NAISS);
        return eleve;
    }

    @BeforeEach
    public void initTest() {
        eleve = createEntity(em);
    }

    @Test
    @Transactional
    void createEleve() throws Exception {
        int databaseSizeBeforeCreate = eleveRepository.findAll().size();
        // Create the Eleve
        restEleveMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eleve))
            )
            .andExpect(status().isCreated());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeCreate + 1);
        Eleve testEleve = eleveList.get(eleveList.size() - 1);
        assertThat(testEleve.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEleve.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testEleve.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testEleve.getDateNaiss()).isEqualTo(DEFAULT_DATE_NAISS);
    }

    @Test
    @Transactional
    void createEleveWithExistingId() throws Exception {
        // Create the Eleve with an existing ID
        eleve.setId(1L);

        int databaseSizeBeforeCreate = eleveRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEleveMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eleve))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEleves() throws Exception {
        // Initialize the database
        eleveRepository.saveAndFlush(eleve);

        // Get all the eleveList
        restEleveMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eleve.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dateNaiss").value(hasItem(DEFAULT_DATE_NAISS.toString())));
    }

    @Test
    @Transactional
    void getEleve() throws Exception {
        // Initialize the database
        eleveRepository.saveAndFlush(eleve);

        // Get the eleve
        restEleveMockMvc
            .perform(get(ENTITY_API_URL_ID, eleve.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eleve.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.dateNaiss").value(DEFAULT_DATE_NAISS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEleve() throws Exception {
        // Get the eleve
        restEleveMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEleve() throws Exception {
        // Initialize the database
        eleveRepository.saveAndFlush(eleve);

        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();

        // Update the eleve
        Eleve updatedEleve = eleveRepository.findById(eleve.getId()).get();
        // Disconnect from session so that the updates on updatedEleve are not directly saved in db
        em.detach(updatedEleve);
        updatedEleve.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).email(UPDATED_EMAIL).dateNaiss(UPDATED_DATE_NAISS);

        restEleveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEleve.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEleve))
            )
            .andExpect(status().isOk());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
        Eleve testEleve = eleveList.get(eleveList.size() - 1);
        assertThat(testEleve.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEleve.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEleve.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEleve.getDateNaiss()).isEqualTo(UPDATED_DATE_NAISS);
    }

    @Test
    @Transactional
    void putNonExistingEleve() throws Exception {
        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();
        eleve.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eleve.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eleve))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEleve() throws Exception {
        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();
        eleve.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eleve))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEleve() throws Exception {
        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();
        eleve.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eleve))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEleveWithPatch() throws Exception {
        // Initialize the database
        eleveRepository.saveAndFlush(eleve);

        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();

        // Update the eleve using partial update
        Eleve partialUpdatedEleve = new Eleve();
        partialUpdatedEleve.setId(eleve.getId());

        partialUpdatedEleve.nom(UPDATED_NOM);

        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEleve.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEleve))
            )
            .andExpect(status().isOk());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
        Eleve testEleve = eleveList.get(eleveList.size() - 1);
        assertThat(testEleve.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEleve.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testEleve.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testEleve.getDateNaiss()).isEqualTo(DEFAULT_DATE_NAISS);
    }

    @Test
    @Transactional
    void fullUpdateEleveWithPatch() throws Exception {
        // Initialize the database
        eleveRepository.saveAndFlush(eleve);

        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();

        // Update the eleve using partial update
        Eleve partialUpdatedEleve = new Eleve();
        partialUpdatedEleve.setId(eleve.getId());

        partialUpdatedEleve.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).email(UPDATED_EMAIL).dateNaiss(UPDATED_DATE_NAISS);

        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEleve.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEleve))
            )
            .andExpect(status().isOk());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
        Eleve testEleve = eleveList.get(eleveList.size() - 1);
        assertThat(testEleve.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEleve.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEleve.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEleve.getDateNaiss()).isEqualTo(UPDATED_DATE_NAISS);
    }

    @Test
    @Transactional
    void patchNonExistingEleve() throws Exception {
        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();
        eleve.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eleve.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eleve))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEleve() throws Exception {
        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();
        eleve.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eleve))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEleve() throws Exception {
        int databaseSizeBeforeUpdate = eleveRepository.findAll().size();
        eleve.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eleve))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Eleve in the database
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEleve() throws Exception {
        // Initialize the database
        eleveRepository.saveAndFlush(eleve);

        int databaseSizeBeforeDelete = eleveRepository.findAll().size();

        // Delete the eleve
        restEleveMockMvc
            .perform(delete(ENTITY_API_URL_ID, eleve.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Eleve> eleveList = eleveRepository.findAll();
        assertThat(eleveList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
