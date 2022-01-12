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
import sn.isi.myschool.domain.Niveau;
import sn.isi.myschool.repository.NiveauRepository;

/**
 * Integration tests for the {@link NiveauResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NiveauResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/niveaus";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNiveauMockMvc;

    private Niveau niveau;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Niveau createEntity(EntityManager em) {
        Niveau niveau = new Niveau().nom(DEFAULT_NOM).libelle(DEFAULT_LIBELLE);
        return niveau;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Niveau createUpdatedEntity(EntityManager em) {
        Niveau niveau = new Niveau().nom(UPDATED_NOM).libelle(UPDATED_LIBELLE);
        return niveau;
    }

    @BeforeEach
    public void initTest() {
        niveau = createEntity(em);
    }

    @Test
    @Transactional
    void createNiveau() throws Exception {
        int databaseSizeBeforeCreate = niveauRepository.findAll().size();
        // Create the Niveau
        restNiveauMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(niveau))
            )
            .andExpect(status().isCreated());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeCreate + 1);
        Niveau testNiveau = niveauList.get(niveauList.size() - 1);
        assertThat(testNiveau.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testNiveau.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void createNiveauWithExistingId() throws Exception {
        // Create the Niveau with an existing ID
        niveau.setId(1L);

        int databaseSizeBeforeCreate = niveauRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNiveauMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(niveau))
            )
            .andExpect(status().isBadRequest());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllNiveaus() throws Exception {
        // Initialize the database
        niveauRepository.saveAndFlush(niveau);

        // Get all the niveauList
        restNiveauMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(niveau.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getNiveau() throws Exception {
        // Initialize the database
        niveauRepository.saveAndFlush(niveau);

        // Get the niveau
        restNiveauMockMvc
            .perform(get(ENTITY_API_URL_ID, niveau.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(niveau.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getNonExistingNiveau() throws Exception {
        // Get the niveau
        restNiveauMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNiveau() throws Exception {
        // Initialize the database
        niveauRepository.saveAndFlush(niveau);

        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();

        // Update the niveau
        Niveau updatedNiveau = niveauRepository.findById(niveau.getId()).get();
        // Disconnect from session so that the updates on updatedNiveau are not directly saved in db
        em.detach(updatedNiveau);
        updatedNiveau.nom(UPDATED_NOM).libelle(UPDATED_LIBELLE);

        restNiveauMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNiveau.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNiveau))
            )
            .andExpect(status().isOk());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
        Niveau testNiveau = niveauList.get(niveauList.size() - 1);
        assertThat(testNiveau.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testNiveau.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void putNonExistingNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();
        niveau.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNiveauMockMvc
            .perform(
                put(ENTITY_API_URL_ID, niveau.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(niveau))
            )
            .andExpect(status().isBadRequest());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();
        niveau.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNiveauMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(niveau))
            )
            .andExpect(status().isBadRequest());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();
        niveau.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNiveauMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(niveau))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNiveauWithPatch() throws Exception {
        // Initialize the database
        niveauRepository.saveAndFlush(niveau);

        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();

        // Update the niveau using partial update
        Niveau partialUpdatedNiveau = new Niveau();
        partialUpdatedNiveau.setId(niveau.getId());

        partialUpdatedNiveau.libelle(UPDATED_LIBELLE);

        restNiveauMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNiveau.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNiveau))
            )
            .andExpect(status().isOk());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
        Niveau testNiveau = niveauList.get(niveauList.size() - 1);
        assertThat(testNiveau.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testNiveau.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateNiveauWithPatch() throws Exception {
        // Initialize the database
        niveauRepository.saveAndFlush(niveau);

        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();

        // Update the niveau using partial update
        Niveau partialUpdatedNiveau = new Niveau();
        partialUpdatedNiveau.setId(niveau.getId());

        partialUpdatedNiveau.nom(UPDATED_NOM).libelle(UPDATED_LIBELLE);

        restNiveauMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNiveau.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNiveau))
            )
            .andExpect(status().isOk());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
        Niveau testNiveau = niveauList.get(niveauList.size() - 1);
        assertThat(testNiveau.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testNiveau.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();
        niveau.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNiveauMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, niveau.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(niveau))
            )
            .andExpect(status().isBadRequest());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();
        niveau.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNiveauMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(niveau))
            )
            .andExpect(status().isBadRequest());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().size();
        niveau.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNiveauMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(niveau))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNiveau() throws Exception {
        // Initialize the database
        niveauRepository.saveAndFlush(niveau);

        int databaseSizeBeforeDelete = niveauRepository.findAll().size();

        // Delete the niveau
        restNiveauMockMvc
            .perform(delete(ENTITY_API_URL_ID, niveau.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Niveau> niveauList = niveauRepository.findAll();
        assertThat(niveauList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
