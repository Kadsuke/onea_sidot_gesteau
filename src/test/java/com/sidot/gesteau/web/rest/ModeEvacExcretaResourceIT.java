package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import com.sidot.gesteau.domain.ModeEvacExcreta;
import com.sidot.gesteau.repository.ModeEvacExcretaRepository;
import com.sidot.gesteau.repository.search.ModeEvacExcretaSearchRepository;
import com.sidot.gesteau.service.criteria.ModeEvacExcretaCriteria;
import com.sidot.gesteau.service.dto.ModeEvacExcretaDTO;
import com.sidot.gesteau.service.mapper.ModeEvacExcretaMapper;
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
 * Integration tests for the {@link ModeEvacExcretaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ModeEvacExcretaResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mode-evac-excretas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/mode-evac-excretas";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ModeEvacExcretaRepository modeEvacExcretaRepository;

    @Autowired
    private ModeEvacExcretaMapper modeEvacExcretaMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.ModeEvacExcretaSearchRepositoryMockConfiguration
     */
    @Autowired
    private ModeEvacExcretaSearchRepository mockModeEvacExcretaSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModeEvacExcretaMockMvc;

    private ModeEvacExcreta modeEvacExcreta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeEvacExcreta createEntity(EntityManager em) {
        ModeEvacExcreta modeEvacExcreta = new ModeEvacExcreta().libelle(DEFAULT_LIBELLE);
        return modeEvacExcreta;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeEvacExcreta createUpdatedEntity(EntityManager em) {
        ModeEvacExcreta modeEvacExcreta = new ModeEvacExcreta().libelle(UPDATED_LIBELLE);
        return modeEvacExcreta;
    }

    @BeforeEach
    public void initTest() {
        modeEvacExcreta = createEntity(em);
    }

    @Test
    @Transactional
    void createModeEvacExcreta() throws Exception {
        int databaseSizeBeforeCreate = modeEvacExcretaRepository.findAll().size();
        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);
        restModeEvacExcretaMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeCreate + 1);
        ModeEvacExcreta testModeEvacExcreta = modeEvacExcretaList.get(modeEvacExcretaList.size() - 1);
        assertThat(testModeEvacExcreta.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(1)).save(testModeEvacExcreta);
    }

    @Test
    @Transactional
    void createModeEvacExcretaWithExistingId() throws Exception {
        // Create the ModeEvacExcreta with an existing ID
        modeEvacExcreta.setId(1L);
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        int databaseSizeBeforeCreate = modeEvacExcretaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restModeEvacExcretaMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeCreate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = modeEvacExcretaRepository.findAll().size();
        // set the field null
        modeEvacExcreta.setLibelle(null);

        // Create the ModeEvacExcreta, which fails.
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        restModeEvacExcretaMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isBadRequest());

        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllModeEvacExcretas() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        // Get all the modeEvacExcretaList
        restModeEvacExcretaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modeEvacExcreta.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getModeEvacExcreta() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        // Get the modeEvacExcreta
        restModeEvacExcretaMockMvc
            .perform(get(ENTITY_API_URL_ID, modeEvacExcreta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(modeEvacExcreta.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getModeEvacExcretasByIdFiltering() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        Long id = modeEvacExcreta.getId();

        defaultModeEvacExcretaShouldBeFound("id.equals=" + id);
        defaultModeEvacExcretaShouldNotBeFound("id.notEquals=" + id);

        defaultModeEvacExcretaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultModeEvacExcretaShouldNotBeFound("id.greaterThan=" + id);

        defaultModeEvacExcretaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultModeEvacExcretaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllModeEvacExcretasByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        // Get all the modeEvacExcretaList where libelle equals to DEFAULT_LIBELLE
        defaultModeEvacExcretaShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the modeEvacExcretaList where libelle equals to UPDATED_LIBELLE
        defaultModeEvacExcretaShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacExcretasByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        // Get all the modeEvacExcretaList where libelle not equals to DEFAULT_LIBELLE
        defaultModeEvacExcretaShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the modeEvacExcretaList where libelle not equals to UPDATED_LIBELLE
        defaultModeEvacExcretaShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacExcretasByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        // Get all the modeEvacExcretaList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultModeEvacExcretaShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the modeEvacExcretaList where libelle equals to UPDATED_LIBELLE
        defaultModeEvacExcretaShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacExcretasByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        // Get all the modeEvacExcretaList where libelle is not null
        defaultModeEvacExcretaShouldBeFound("libelle.specified=true");

        // Get all the modeEvacExcretaList where libelle is null
        defaultModeEvacExcretaShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllModeEvacExcretasByLibelleContainsSomething() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        // Get all the modeEvacExcretaList where libelle contains DEFAULT_LIBELLE
        defaultModeEvacExcretaShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the modeEvacExcretaList where libelle contains UPDATED_LIBELLE
        defaultModeEvacExcretaShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacExcretasByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        // Get all the modeEvacExcretaList where libelle does not contain DEFAULT_LIBELLE
        defaultModeEvacExcretaShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the modeEvacExcretaList where libelle does not contain UPDATED_LIBELLE
        defaultModeEvacExcretaShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacExcretasByFicheSuiviOuvrageIsEqualToSomething() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);
        FicheSuiviOuvrage ficheSuiviOuvrage = FicheSuiviOuvrageResourceIT.createEntity(em);
        em.persist(ficheSuiviOuvrage);
        em.flush();
        modeEvacExcreta.addFicheSuiviOuvrage(ficheSuiviOuvrage);
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);
        Long ficheSuiviOuvrageId = ficheSuiviOuvrage.getId();

        // Get all the modeEvacExcretaList where ficheSuiviOuvrage equals to ficheSuiviOuvrageId
        defaultModeEvacExcretaShouldBeFound("ficheSuiviOuvrageId.equals=" + ficheSuiviOuvrageId);

        // Get all the modeEvacExcretaList where ficheSuiviOuvrage equals to (ficheSuiviOuvrageId + 1)
        defaultModeEvacExcretaShouldNotBeFound("ficheSuiviOuvrageId.equals=" + (ficheSuiviOuvrageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultModeEvacExcretaShouldBeFound(String filter) throws Exception {
        restModeEvacExcretaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modeEvacExcreta.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));

        // Check, that the count call also returns 1
        restModeEvacExcretaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultModeEvacExcretaShouldNotBeFound(String filter) throws Exception {
        restModeEvacExcretaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restModeEvacExcretaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingModeEvacExcreta() throws Exception {
        // Get the modeEvacExcreta
        restModeEvacExcretaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewModeEvacExcreta() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();

        // Update the modeEvacExcreta
        ModeEvacExcreta updatedModeEvacExcreta = modeEvacExcretaRepository.findById(modeEvacExcreta.getId()).get();
        // Disconnect from session so that the updates on updatedModeEvacExcreta are not directly saved in db
        em.detach(updatedModeEvacExcreta);
        updatedModeEvacExcreta.libelle(UPDATED_LIBELLE);
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(updatedModeEvacExcreta);

        restModeEvacExcretaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modeEvacExcretaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isOk());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacExcreta testModeEvacExcreta = modeEvacExcretaList.get(modeEvacExcretaList.size() - 1);
        assertThat(testModeEvacExcreta.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository).save(testModeEvacExcreta);
    }

    @Test
    @Transactional
    void putNonExistingModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModeEvacExcretaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modeEvacExcretaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    @Transactional
    void putWithIdMismatchModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeEvacExcretaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeEvacExcretaMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    @Transactional
    void partialUpdateModeEvacExcretaWithPatch() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();

        // Update the modeEvacExcreta using partial update
        ModeEvacExcreta partialUpdatedModeEvacExcreta = new ModeEvacExcreta();
        partialUpdatedModeEvacExcreta.setId(modeEvacExcreta.getId());

        partialUpdatedModeEvacExcreta.libelle(UPDATED_LIBELLE);

        restModeEvacExcretaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModeEvacExcreta.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedModeEvacExcreta))
            )
            .andExpect(status().isOk());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacExcreta testModeEvacExcreta = modeEvacExcretaList.get(modeEvacExcretaList.size() - 1);
        assertThat(testModeEvacExcreta.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateModeEvacExcretaWithPatch() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();

        // Update the modeEvacExcreta using partial update
        ModeEvacExcreta partialUpdatedModeEvacExcreta = new ModeEvacExcreta();
        partialUpdatedModeEvacExcreta.setId(modeEvacExcreta.getId());

        partialUpdatedModeEvacExcreta.libelle(UPDATED_LIBELLE);

        restModeEvacExcretaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModeEvacExcreta.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedModeEvacExcreta))
            )
            .andExpect(status().isOk());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacExcreta testModeEvacExcreta = modeEvacExcretaList.get(modeEvacExcretaList.size() - 1);
        assertThat(testModeEvacExcreta.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModeEvacExcretaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, modeEvacExcretaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeEvacExcretaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeEvacExcretaMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    @Transactional
    void deleteModeEvacExcreta() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);

        int databaseSizeBeforeDelete = modeEvacExcretaRepository.findAll().size();

        // Delete the modeEvacExcreta
        restModeEvacExcretaMockMvc
            .perform(delete(ENTITY_API_URL_ID, modeEvacExcreta.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(1)).deleteById(modeEvacExcreta.getId());
    }

    @Test
    @Transactional
    void searchModeEvacExcreta() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        modeEvacExcretaRepository.saveAndFlush(modeEvacExcreta);
        when(mockModeEvacExcretaSearchRepository.search(queryStringQuery("id:" + modeEvacExcreta.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(modeEvacExcreta), PageRequest.of(0, 1), 1));

        // Search the modeEvacExcreta
        restModeEvacExcretaMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + modeEvacExcreta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modeEvacExcreta.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }
}
