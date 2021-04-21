package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import com.sidot.gesteau.domain.SourceApprovEp;
import com.sidot.gesteau.repository.SourceApprovEpRepository;
import com.sidot.gesteau.repository.search.SourceApprovEpSearchRepository;
import com.sidot.gesteau.service.criteria.SourceApprovEpCriteria;
import com.sidot.gesteau.service.dto.SourceApprovEpDTO;
import com.sidot.gesteau.service.mapper.SourceApprovEpMapper;
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
 * Integration tests for the {@link SourceApprovEpResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SourceApprovEpResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/source-approv-eps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/source-approv-eps";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SourceApprovEpRepository sourceApprovEpRepository;

    @Autowired
    private SourceApprovEpMapper sourceApprovEpMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.SourceApprovEpSearchRepositoryMockConfiguration
     */
    @Autowired
    private SourceApprovEpSearchRepository mockSourceApprovEpSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSourceApprovEpMockMvc;

    private SourceApprovEp sourceApprovEp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SourceApprovEp createEntity(EntityManager em) {
        SourceApprovEp sourceApprovEp = new SourceApprovEp().libelle(DEFAULT_LIBELLE);
        return sourceApprovEp;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SourceApprovEp createUpdatedEntity(EntityManager em) {
        SourceApprovEp sourceApprovEp = new SourceApprovEp().libelle(UPDATED_LIBELLE);
        return sourceApprovEp;
    }

    @BeforeEach
    public void initTest() {
        sourceApprovEp = createEntity(em);
    }

    @Test
    @Transactional
    void createSourceApprovEp() throws Exception {
        int databaseSizeBeforeCreate = sourceApprovEpRepository.findAll().size();
        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);
        restSourceApprovEpMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeCreate + 1);
        SourceApprovEp testSourceApprovEp = sourceApprovEpList.get(sourceApprovEpList.size() - 1);
        assertThat(testSourceApprovEp.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(1)).save(testSourceApprovEp);
    }

    @Test
    @Transactional
    void createSourceApprovEpWithExistingId() throws Exception {
        // Create the SourceApprovEp with an existing ID
        sourceApprovEp.setId(1L);
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        int databaseSizeBeforeCreate = sourceApprovEpRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSourceApprovEpMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeCreate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = sourceApprovEpRepository.findAll().size();
        // set the field null
        sourceApprovEp.setLibelle(null);

        // Create the SourceApprovEp, which fails.
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        restSourceApprovEpMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isBadRequest());

        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSourceApprovEps() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        // Get all the sourceApprovEpList
        restSourceApprovEpMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sourceApprovEp.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getSourceApprovEp() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        // Get the sourceApprovEp
        restSourceApprovEpMockMvc
            .perform(get(ENTITY_API_URL_ID, sourceApprovEp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sourceApprovEp.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getSourceApprovEpsByIdFiltering() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        Long id = sourceApprovEp.getId();

        defaultSourceApprovEpShouldBeFound("id.equals=" + id);
        defaultSourceApprovEpShouldNotBeFound("id.notEquals=" + id);

        defaultSourceApprovEpShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSourceApprovEpShouldNotBeFound("id.greaterThan=" + id);

        defaultSourceApprovEpShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSourceApprovEpShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSourceApprovEpsByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        // Get all the sourceApprovEpList where libelle equals to DEFAULT_LIBELLE
        defaultSourceApprovEpShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the sourceApprovEpList where libelle equals to UPDATED_LIBELLE
        defaultSourceApprovEpShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSourceApprovEpsByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        // Get all the sourceApprovEpList where libelle not equals to DEFAULT_LIBELLE
        defaultSourceApprovEpShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the sourceApprovEpList where libelle not equals to UPDATED_LIBELLE
        defaultSourceApprovEpShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSourceApprovEpsByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        // Get all the sourceApprovEpList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultSourceApprovEpShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the sourceApprovEpList where libelle equals to UPDATED_LIBELLE
        defaultSourceApprovEpShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSourceApprovEpsByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        // Get all the sourceApprovEpList where libelle is not null
        defaultSourceApprovEpShouldBeFound("libelle.specified=true");

        // Get all the sourceApprovEpList where libelle is null
        defaultSourceApprovEpShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllSourceApprovEpsByLibelleContainsSomething() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        // Get all the sourceApprovEpList where libelle contains DEFAULT_LIBELLE
        defaultSourceApprovEpShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the sourceApprovEpList where libelle contains UPDATED_LIBELLE
        defaultSourceApprovEpShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSourceApprovEpsByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        // Get all the sourceApprovEpList where libelle does not contain DEFAULT_LIBELLE
        defaultSourceApprovEpShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the sourceApprovEpList where libelle does not contain UPDATED_LIBELLE
        defaultSourceApprovEpShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSourceApprovEpsByFicheSuiviOuvrageIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);
        FicheSuiviOuvrage ficheSuiviOuvrage = FicheSuiviOuvrageResourceIT.createEntity(em);
        em.persist(ficheSuiviOuvrage);
        em.flush();
        sourceApprovEp.addFicheSuiviOuvrage(ficheSuiviOuvrage);
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);
        Long ficheSuiviOuvrageId = ficheSuiviOuvrage.getId();

        // Get all the sourceApprovEpList where ficheSuiviOuvrage equals to ficheSuiviOuvrageId
        defaultSourceApprovEpShouldBeFound("ficheSuiviOuvrageId.equals=" + ficheSuiviOuvrageId);

        // Get all the sourceApprovEpList where ficheSuiviOuvrage equals to (ficheSuiviOuvrageId + 1)
        defaultSourceApprovEpShouldNotBeFound("ficheSuiviOuvrageId.equals=" + (ficheSuiviOuvrageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSourceApprovEpShouldBeFound(String filter) throws Exception {
        restSourceApprovEpMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sourceApprovEp.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));

        // Check, that the count call also returns 1
        restSourceApprovEpMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSourceApprovEpShouldNotBeFound(String filter) throws Exception {
        restSourceApprovEpMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSourceApprovEpMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSourceApprovEp() throws Exception {
        // Get the sourceApprovEp
        restSourceApprovEpMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSourceApprovEp() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();

        // Update the sourceApprovEp
        SourceApprovEp updatedSourceApprovEp = sourceApprovEpRepository.findById(sourceApprovEp.getId()).get();
        // Disconnect from session so that the updates on updatedSourceApprovEp are not directly saved in db
        em.detach(updatedSourceApprovEp);
        updatedSourceApprovEp.libelle(UPDATED_LIBELLE);
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(updatedSourceApprovEp);

        restSourceApprovEpMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sourceApprovEpDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isOk());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);
        SourceApprovEp testSourceApprovEp = sourceApprovEpList.get(sourceApprovEpList.size() - 1);
        assertThat(testSourceApprovEp.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository).save(testSourceApprovEp);
    }

    @Test
    @Transactional
    void putNonExistingSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSourceApprovEpMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sourceApprovEpDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    @Transactional
    void putWithIdMismatchSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSourceApprovEpMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSourceApprovEpMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    @Transactional
    void partialUpdateSourceApprovEpWithPatch() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();

        // Update the sourceApprovEp using partial update
        SourceApprovEp partialUpdatedSourceApprovEp = new SourceApprovEp();
        partialUpdatedSourceApprovEp.setId(sourceApprovEp.getId());

        restSourceApprovEpMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSourceApprovEp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSourceApprovEp))
            )
            .andExpect(status().isOk());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);
        SourceApprovEp testSourceApprovEp = sourceApprovEpList.get(sourceApprovEpList.size() - 1);
        assertThat(testSourceApprovEp.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateSourceApprovEpWithPatch() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();

        // Update the sourceApprovEp using partial update
        SourceApprovEp partialUpdatedSourceApprovEp = new SourceApprovEp();
        partialUpdatedSourceApprovEp.setId(sourceApprovEp.getId());

        partialUpdatedSourceApprovEp.libelle(UPDATED_LIBELLE);

        restSourceApprovEpMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSourceApprovEp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSourceApprovEp))
            )
            .andExpect(status().isOk());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);
        SourceApprovEp testSourceApprovEp = sourceApprovEpList.get(sourceApprovEpList.size() - 1);
        assertThat(testSourceApprovEp.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSourceApprovEpMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sourceApprovEpDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSourceApprovEpMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSourceApprovEpMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    @Transactional
    void deleteSourceApprovEp() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);

        int databaseSizeBeforeDelete = sourceApprovEpRepository.findAll().size();

        // Delete the sourceApprovEp
        restSourceApprovEpMockMvc
            .perform(delete(ENTITY_API_URL_ID, sourceApprovEp.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(1)).deleteById(sourceApprovEp.getId());
    }

    @Test
    @Transactional
    void searchSourceApprovEp() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        sourceApprovEpRepository.saveAndFlush(sourceApprovEp);
        when(mockSourceApprovEpSearchRepository.search(queryStringQuery("id:" + sourceApprovEp.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(sourceApprovEp), PageRequest.of(0, 1), 1));

        // Search the sourceApprovEp
        restSourceApprovEpMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sourceApprovEp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sourceApprovEp.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }
}
