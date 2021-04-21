package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import com.sidot.gesteau.domain.Prefabricant;
import com.sidot.gesteau.repository.PrefabricantRepository;
import com.sidot.gesteau.repository.search.PrefabricantSearchRepository;
import com.sidot.gesteau.service.criteria.PrefabricantCriteria;
import com.sidot.gesteau.service.dto.PrefabricantDTO;
import com.sidot.gesteau.service.mapper.PrefabricantMapper;
import java.util.Collections;
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

/**
 * Integration tests for the {@link PrefabricantResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PrefabricantResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/prefabricants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/prefabricants";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PrefabricantRepository prefabricantRepository;

    @Autowired
    private PrefabricantMapper prefabricantMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.PrefabricantSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrefabricantSearchRepository mockPrefabricantSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrefabricantMockMvc;

    private Prefabricant prefabricant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prefabricant createEntity(EntityManager em) {
        Prefabricant prefabricant = new Prefabricant().libelle(DEFAULT_LIBELLE);
        return prefabricant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prefabricant createUpdatedEntity(EntityManager em) {
        Prefabricant prefabricant = new Prefabricant().libelle(UPDATED_LIBELLE);
        return prefabricant;
    }

    @BeforeEach
    public void initTest() {
        prefabricant = createEntity(em);
    }

    @Test
    @Transactional
    void createPrefabricant() throws Exception {
        int databaseSizeBeforeCreate = prefabricantRepository.findAll().size();
        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);
        restPrefabricantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeCreate + 1);
        Prefabricant testPrefabricant = prefabricantList.get(prefabricantList.size() - 1);
        assertThat(testPrefabricant.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(1)).save(testPrefabricant);
    }

    @Test
    @Transactional
    void createPrefabricantWithExistingId() throws Exception {
        // Create the Prefabricant with an existing ID
        prefabricant.setId(1L);
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        int databaseSizeBeforeCreate = prefabricantRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrefabricantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeCreate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = prefabricantRepository.findAll().size();
        // set the field null
        prefabricant.setLibelle(null);

        // Create the Prefabricant, which fails.
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        restPrefabricantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isBadRequest());

        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPrefabricants() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        // Get all the prefabricantList
        restPrefabricantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prefabricant.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getPrefabricant() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        // Get the prefabricant
        restPrefabricantMockMvc
            .perform(get(ENTITY_API_URL_ID, prefabricant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prefabricant.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getPrefabricantsByIdFiltering() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        Long id = prefabricant.getId();

        defaultPrefabricantShouldBeFound("id.equals=" + id);
        defaultPrefabricantShouldNotBeFound("id.notEquals=" + id);

        defaultPrefabricantShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrefabricantShouldNotBeFound("id.greaterThan=" + id);

        defaultPrefabricantShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrefabricantShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPrefabricantsByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        // Get all the prefabricantList where libelle equals to DEFAULT_LIBELLE
        defaultPrefabricantShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the prefabricantList where libelle equals to UPDATED_LIBELLE
        defaultPrefabricantShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPrefabricantsByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        // Get all the prefabricantList where libelle not equals to DEFAULT_LIBELLE
        defaultPrefabricantShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the prefabricantList where libelle not equals to UPDATED_LIBELLE
        defaultPrefabricantShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPrefabricantsByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        // Get all the prefabricantList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultPrefabricantShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the prefabricantList where libelle equals to UPDATED_LIBELLE
        defaultPrefabricantShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPrefabricantsByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        // Get all the prefabricantList where libelle is not null
        defaultPrefabricantShouldBeFound("libelle.specified=true");

        // Get all the prefabricantList where libelle is null
        defaultPrefabricantShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllPrefabricantsByLibelleContainsSomething() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        // Get all the prefabricantList where libelle contains DEFAULT_LIBELLE
        defaultPrefabricantShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the prefabricantList where libelle contains UPDATED_LIBELLE
        defaultPrefabricantShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPrefabricantsByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        // Get all the prefabricantList where libelle does not contain DEFAULT_LIBELLE
        defaultPrefabricantShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the prefabricantList where libelle does not contain UPDATED_LIBELLE
        defaultPrefabricantShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPrefabricantsByFicheSuiviOuvrageIsEqualToSomething() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);
        FicheSuiviOuvrage ficheSuiviOuvrage = FicheSuiviOuvrageResourceIT.createEntity(em);
        em.persist(ficheSuiviOuvrage);
        em.flush();
        prefabricant.addFicheSuiviOuvrage(ficheSuiviOuvrage);
        prefabricantRepository.saveAndFlush(prefabricant);
        Long ficheSuiviOuvrageId = ficheSuiviOuvrage.getId();

        // Get all the prefabricantList where ficheSuiviOuvrage equals to ficheSuiviOuvrageId
        defaultPrefabricantShouldBeFound("ficheSuiviOuvrageId.equals=" + ficheSuiviOuvrageId);

        // Get all the prefabricantList where ficheSuiviOuvrage equals to (ficheSuiviOuvrageId + 1)
        defaultPrefabricantShouldNotBeFound("ficheSuiviOuvrageId.equals=" + (ficheSuiviOuvrageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrefabricantShouldBeFound(String filter) throws Exception {
        restPrefabricantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prefabricant.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));

        // Check, that the count call also returns 1
        restPrefabricantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrefabricantShouldNotBeFound(String filter) throws Exception {
        restPrefabricantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrefabricantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPrefabricant() throws Exception {
        // Get the prefabricant
        restPrefabricantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPrefabricant() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();

        // Update the prefabricant
        Prefabricant updatedPrefabricant = prefabricantRepository.findById(prefabricant.getId()).get();
        // Disconnect from session so that the updates on updatedPrefabricant are not directly saved in db
        em.detach(updatedPrefabricant);
        updatedPrefabricant.libelle(UPDATED_LIBELLE);
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(updatedPrefabricant);

        restPrefabricantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, prefabricantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);
        Prefabricant testPrefabricant = prefabricantList.get(prefabricantList.size() - 1);
        assertThat(testPrefabricant.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository).save(testPrefabricant);
    }

    @Test
    @Transactional
    void putNonExistingPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrefabricantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, prefabricantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    @Transactional
    void putWithIdMismatchPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrefabricantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrefabricantMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    @Transactional
    void partialUpdatePrefabricantWithPatch() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();

        // Update the prefabricant using partial update
        Prefabricant partialUpdatedPrefabricant = new Prefabricant();
        partialUpdatedPrefabricant.setId(prefabricant.getId());

        restPrefabricantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrefabricant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrefabricant))
            )
            .andExpect(status().isOk());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);
        Prefabricant testPrefabricant = prefabricantList.get(prefabricantList.size() - 1);
        assertThat(testPrefabricant.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdatePrefabricantWithPatch() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();

        // Update the prefabricant using partial update
        Prefabricant partialUpdatedPrefabricant = new Prefabricant();
        partialUpdatedPrefabricant.setId(prefabricant.getId());

        partialUpdatedPrefabricant.libelle(UPDATED_LIBELLE);

        restPrefabricantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrefabricant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrefabricant))
            )
            .andExpect(status().isOk());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);
        Prefabricant testPrefabricant = prefabricantList.get(prefabricantList.size() - 1);
        assertThat(testPrefabricant.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrefabricantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, prefabricantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrefabricantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrefabricantMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    @Transactional
    void deletePrefabricant() throws Exception {
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);

        int databaseSizeBeforeDelete = prefabricantRepository.findAll().size();

        // Delete the prefabricant
        restPrefabricantMockMvc
            .perform(delete(ENTITY_API_URL_ID, prefabricant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(1)).deleteById(prefabricant.getId());
    }

    @Test
    @Transactional
    void searchPrefabricant() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        prefabricantRepository.saveAndFlush(prefabricant);
        when(mockPrefabricantSearchRepository.search(queryStringQuery("id:" + prefabricant.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(prefabricant), PageRequest.of(0, 1), 1));

        // Search the prefabricant
        restPrefabricantMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + prefabricant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prefabricant.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }
}
