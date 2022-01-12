package sn.isi.myschool.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.myschool.IntegrationTest;
import sn.isi.myschool.domain.Enseignant;
import sn.isi.myschool.repository.EnseignantRepository;
import sn.isi.myschool.service.EnseignantService;

/**
 * Integration tests for the {@link EnseignantResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EnseignantResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_NAISS = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_NAISS = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/enseignants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Mock
    private EnseignantRepository enseignantRepositoryMock;

    @Mock
    private EnseignantService enseignantServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnseignantMockMvc;

    private Enseignant enseignant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enseignant createEntity(EntityManager em) {
        Enseignant enseignant = new Enseignant().nom(DEFAULT_NOM).prenom(DEFAULT_PRENOM).email(DEFAULT_EMAIL).dateNaiss(DEFAULT_DATE_NAISS);
        return enseignant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enseignant createUpdatedEntity(EntityManager em) {
        Enseignant enseignant = new Enseignant().nom(UPDATED_NOM).prenom(UPDATED_PRENOM).email(UPDATED_EMAIL).dateNaiss(UPDATED_DATE_NAISS);
        return enseignant;
    }

    @BeforeEach
    public void initTest() {
        enseignant = createEntity(em);
    }

    @Test
    @Transactional
    void createEnseignant() throws Exception {
        int databaseSizeBeforeCreate = enseignantRepository.findAll().size();
        // Create the Enseignant
        restEnseignantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enseignant))
            )
            .andExpect(status().isCreated());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeCreate + 1);
        Enseignant testEnseignant = enseignantList.get(enseignantList.size() - 1);
        assertThat(testEnseignant.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEnseignant.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testEnseignant.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testEnseignant.getDateNaiss()).isEqualTo(DEFAULT_DATE_NAISS);
    }

    @Test
    @Transactional
    void createEnseignantWithExistingId() throws Exception {
        // Create the Enseignant with an existing ID
        enseignant.setId(1L);

        int databaseSizeBeforeCreate = enseignantRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnseignantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enseignant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEnseignants() throws Exception {
        // Initialize the database
        enseignantRepository.saveAndFlush(enseignant);

        // Get all the enseignantList
        restEnseignantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enseignant.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dateNaiss").value(hasItem(DEFAULT_DATE_NAISS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnseignantsWithEagerRelationshipsIsEnabled() throws Exception {
        when(enseignantServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnseignantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(enseignantServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnseignantsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(enseignantServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnseignantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(enseignantServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getEnseignant() throws Exception {
        // Initialize the database
        enseignantRepository.saveAndFlush(enseignant);

        // Get the enseignant
        restEnseignantMockMvc
            .perform(get(ENTITY_API_URL_ID, enseignant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(enseignant.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.dateNaiss").value(DEFAULT_DATE_NAISS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEnseignant() throws Exception {
        // Get the enseignant
        restEnseignantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEnseignant() throws Exception {
        // Initialize the database
        enseignantRepository.saveAndFlush(enseignant);

        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();

        // Update the enseignant
        Enseignant updatedEnseignant = enseignantRepository.findById(enseignant.getId()).get();
        // Disconnect from session so that the updates on updatedEnseignant are not directly saved in db
        em.detach(updatedEnseignant);
        updatedEnseignant.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).email(UPDATED_EMAIL).dateNaiss(UPDATED_DATE_NAISS);

        restEnseignantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEnseignant.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEnseignant))
            )
            .andExpect(status().isOk());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
        Enseignant testEnseignant = enseignantList.get(enseignantList.size() - 1);
        assertThat(testEnseignant.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEnseignant.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEnseignant.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEnseignant.getDateNaiss()).isEqualTo(UPDATED_DATE_NAISS);
    }

    @Test
    @Transactional
    void putNonExistingEnseignant() throws Exception {
        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();
        enseignant.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnseignantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enseignant.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enseignant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnseignant() throws Exception {
        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();
        enseignant.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnseignantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enseignant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnseignant() throws Exception {
        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();
        enseignant.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnseignantMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enseignant))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnseignantWithPatch() throws Exception {
        // Initialize the database
        enseignantRepository.saveAndFlush(enseignant);

        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();

        // Update the enseignant using partial update
        Enseignant partialUpdatedEnseignant = new Enseignant();
        partialUpdatedEnseignant.setId(enseignant.getId());

        partialUpdatedEnseignant.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).email(UPDATED_EMAIL).dateNaiss(UPDATED_DATE_NAISS);

        restEnseignantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnseignant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnseignant))
            )
            .andExpect(status().isOk());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
        Enseignant testEnseignant = enseignantList.get(enseignantList.size() - 1);
        assertThat(testEnseignant.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEnseignant.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEnseignant.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEnseignant.getDateNaiss()).isEqualTo(UPDATED_DATE_NAISS);
    }

    @Test
    @Transactional
    void fullUpdateEnseignantWithPatch() throws Exception {
        // Initialize the database
        enseignantRepository.saveAndFlush(enseignant);

        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();

        // Update the enseignant using partial update
        Enseignant partialUpdatedEnseignant = new Enseignant();
        partialUpdatedEnseignant.setId(enseignant.getId());

        partialUpdatedEnseignant.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).email(UPDATED_EMAIL).dateNaiss(UPDATED_DATE_NAISS);

        restEnseignantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnseignant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnseignant))
            )
            .andExpect(status().isOk());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
        Enseignant testEnseignant = enseignantList.get(enseignantList.size() - 1);
        assertThat(testEnseignant.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEnseignant.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEnseignant.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEnseignant.getDateNaiss()).isEqualTo(UPDATED_DATE_NAISS);
    }

    @Test
    @Transactional
    void patchNonExistingEnseignant() throws Exception {
        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();
        enseignant.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnseignantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, enseignant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enseignant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnseignant() throws Exception {
        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();
        enseignant.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnseignantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enseignant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnseignant() throws Exception {
        int databaseSizeBeforeUpdate = enseignantRepository.findAll().size();
        enseignant.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnseignantMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enseignant))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enseignant in the database
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEnseignant() throws Exception {
        // Initialize the database
        enseignantRepository.saveAndFlush(enseignant);

        int databaseSizeBeforeDelete = enseignantRepository.findAll().size();

        // Delete the enseignant
        restEnseignantMockMvc
            .perform(delete(ENTITY_API_URL_ID, enseignant.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Enseignant> enseignantList = enseignantRepository.findAll();
        assertThat(enseignantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
