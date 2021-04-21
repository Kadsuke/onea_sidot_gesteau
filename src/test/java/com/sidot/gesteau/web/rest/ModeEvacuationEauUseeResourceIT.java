package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import com.sidot.gesteau.domain.ModeEvacuationEauUsee;
import com.sidot.gesteau.repository.ModeEvacuationEauUseeRepository;
import com.sidot.gesteau.repository.search.ModeEvacuationEauUseeSearchRepository;
import com.sidot.gesteau.service.criteria.ModeEvacuationEauUseeCriteria;
import com.sidot.gesteau.service.dto.ModeEvacuationEauUseeDTO;
import com.sidot.gesteau.service.mapper.ModeEvacuationEauUseeMapper;
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
 * Integration tests for the {@link ModeEvacuationEauUseeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ModeEvacuationEauUseeResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mode-evacuation-eau-usees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/mode-evacuation-eau-usees";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ModeEvacuationEauUseeRepository modeEvacuationEauUseeRepository;

    @Autowired
    private ModeEvacuationEauUseeMapper modeEvacuationEauUseeMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.ModeEvacuationEauUseeSearchRepositoryMockConfiguration
     */
    @Autowired
    private ModeEvacuationEauUseeSearchRepository mockModeEvacuationEauUseeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModeEvacuationEauUseeMockMvc;

    private ModeEvacuationEauUsee modeEvacuationEauUsee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeEvacuationEauUsee createEntity(EntityManager em) {
        ModeEvacuationEauUsee modeEvacuationEauUsee = new ModeEvacuationEauUsee().libelle(DEFAULT_LIBELLE);
        return modeEvacuationEauUsee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeEvacuationEauUsee createUpdatedEntity(EntityManager em) {
        ModeEvacuationEauUsee modeEvacuationEauUsee = new ModeEvacuationEauUsee().libelle(UPDATED_LIBELLE);
        return modeEvacuationEauUsee;
    }

    @BeforeEach
    public void initTest() {
        modeEvacuationEauUsee = createEntity(em);
    }

    @Test
    @Transactional
    void createModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeCreate = modeEvacuationEauUseeRepository.findAll().size();
        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);
        restModeEvacuationEauUseeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeCreate + 1);
        ModeEvacuationEauUsee testModeEvacuationEauUsee = modeEvacuationEauUseeList.get(modeEvacuationEauUseeList.size() - 1);
        assertThat(testModeEvacuationEauUsee.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(1)).save(testModeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void createModeEvacuationEauUseeWithExistingId() throws Exception {
        // Create the ModeEvacuationEauUsee with an existing ID
        modeEvacuationEauUsee.setId(1L);
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        int databaseSizeBeforeCreate = modeEvacuationEauUseeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restModeEvacuationEauUseeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeCreate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = modeEvacuationEauUseeRepository.findAll().size();
        // set the field null
        modeEvacuationEauUsee.setLibelle(null);

        // Create the ModeEvacuationEauUsee, which fails.
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        restModeEvacuationEauUseeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isBadRequest());

        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllModeEvacuationEauUsees() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        // Get all the modeEvacuationEauUseeList
        restModeEvacuationEauUseeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modeEvacuationEauUsee.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getModeEvacuationEauUsee() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        // Get the modeEvacuationEauUsee
        restModeEvacuationEauUseeMockMvc
            .perform(get(ENTITY_API_URL_ID, modeEvacuationEauUsee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(modeEvacuationEauUsee.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getModeEvacuationEauUseesByIdFiltering() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        Long id = modeEvacuationEauUsee.getId();

        defaultModeEvacuationEauUseeShouldBeFound("id.equals=" + id);
        defaultModeEvacuationEauUseeShouldNotBeFound("id.notEquals=" + id);

        defaultModeEvacuationEauUseeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultModeEvacuationEauUseeShouldNotBeFound("id.greaterThan=" + id);

        defaultModeEvacuationEauUseeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultModeEvacuationEauUseeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllModeEvacuationEauUseesByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        // Get all the modeEvacuationEauUseeList where libelle equals to DEFAULT_LIBELLE
        defaultModeEvacuationEauUseeShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the modeEvacuationEauUseeList where libelle equals to UPDATED_LIBELLE
        defaultModeEvacuationEauUseeShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacuationEauUseesByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        // Get all the modeEvacuationEauUseeList where libelle not equals to DEFAULT_LIBELLE
        defaultModeEvacuationEauUseeShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the modeEvacuationEauUseeList where libelle not equals to UPDATED_LIBELLE
        defaultModeEvacuationEauUseeShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacuationEauUseesByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        // Get all the modeEvacuationEauUseeList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultModeEvacuationEauUseeShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the modeEvacuationEauUseeList where libelle equals to UPDATED_LIBELLE
        defaultModeEvacuationEauUseeShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacuationEauUseesByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        // Get all the modeEvacuationEauUseeList where libelle is not null
        defaultModeEvacuationEauUseeShouldBeFound("libelle.specified=true");

        // Get all the modeEvacuationEauUseeList where libelle is null
        defaultModeEvacuationEauUseeShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllModeEvacuationEauUseesByLibelleContainsSomething() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        // Get all the modeEvacuationEauUseeList where libelle contains DEFAULT_LIBELLE
        defaultModeEvacuationEauUseeShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the modeEvacuationEauUseeList where libelle contains UPDATED_LIBELLE
        defaultModeEvacuationEauUseeShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacuationEauUseesByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        // Get all the modeEvacuationEauUseeList where libelle does not contain DEFAULT_LIBELLE
        defaultModeEvacuationEauUseeShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the modeEvacuationEauUseeList where libelle does not contain UPDATED_LIBELLE
        defaultModeEvacuationEauUseeShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModeEvacuationEauUseesByFicheSuiviOuvrageIsEqualToSomething() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);
        FicheSuiviOuvrage ficheSuiviOuvrage = FicheSuiviOuvrageResourceIT.createEntity(em);
        em.persist(ficheSuiviOuvrage);
        em.flush();
        modeEvacuationEauUsee.addFicheSuiviOuvrage(ficheSuiviOuvrage);
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);
        Long ficheSuiviOuvrageId = ficheSuiviOuvrage.getId();

        // Get all the modeEvacuationEauUseeList where ficheSuiviOuvrage equals to ficheSuiviOuvrageId
        defaultModeEvacuationEauUseeShouldBeFound("ficheSuiviOuvrageId.equals=" + ficheSuiviOuvrageId);

        // Get all the modeEvacuationEauUseeList where ficheSuiviOuvrage equals to (ficheSuiviOuvrageId + 1)
        defaultModeEvacuationEauUseeShouldNotBeFound("ficheSuiviOuvrageId.equals=" + (ficheSuiviOuvrageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultModeEvacuationEauUseeShouldBeFound(String filter) throws Exception {
        restModeEvacuationEauUseeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modeEvacuationEauUsee.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));

        // Check, that the count call also returns 1
        restModeEvacuationEauUseeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultModeEvacuationEauUseeShouldNotBeFound(String filter) throws Exception {
        restModeEvacuationEauUseeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restModeEvacuationEauUseeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingModeEvacuationEauUsee() throws Exception {
        // Get the modeEvacuationEauUsee
        restModeEvacuationEauUseeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewModeEvacuationEauUsee() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();

        // Update the modeEvacuationEauUsee
        ModeEvacuationEauUsee updatedModeEvacuationEauUsee = modeEvacuationEauUseeRepository.findById(modeEvacuationEauUsee.getId()).get();
        // Disconnect from session so that the updates on updatedModeEvacuationEauUsee are not directly saved in db
        em.detach(updatedModeEvacuationEauUsee);
        updatedModeEvacuationEauUsee.libelle(UPDATED_LIBELLE);
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(updatedModeEvacuationEauUsee);

        restModeEvacuationEauUseeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modeEvacuationEauUseeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isOk());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacuationEauUsee testModeEvacuationEauUsee = modeEvacuationEauUseeList.get(modeEvacuationEauUseeList.size() - 1);
        assertThat(testModeEvacuationEauUsee.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository).save(testModeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void putNonExistingModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModeEvacuationEauUseeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modeEvacuationEauUseeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void putWithIdMismatchModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeEvacuationEauUseeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeEvacuationEauUseeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void partialUpdateModeEvacuationEauUseeWithPatch() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();

        // Update the modeEvacuationEauUsee using partial update
        ModeEvacuationEauUsee partialUpdatedModeEvacuationEauUsee = new ModeEvacuationEauUsee();
        partialUpdatedModeEvacuationEauUsee.setId(modeEvacuationEauUsee.getId());

        partialUpdatedModeEvacuationEauUsee.libelle(UPDATED_LIBELLE);

        restModeEvacuationEauUseeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModeEvacuationEauUsee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedModeEvacuationEauUsee))
            )
            .andExpect(status().isOk());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacuationEauUsee testModeEvacuationEauUsee = modeEvacuationEauUseeList.get(modeEvacuationEauUseeList.size() - 1);
        assertThat(testModeEvacuationEauUsee.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateModeEvacuationEauUseeWithPatch() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();

        // Update the modeEvacuationEauUsee using partial update
        ModeEvacuationEauUsee partialUpdatedModeEvacuationEauUsee = new ModeEvacuationEauUsee();
        partialUpdatedModeEvacuationEauUsee.setId(modeEvacuationEauUsee.getId());

        partialUpdatedModeEvacuationEauUsee.libelle(UPDATED_LIBELLE);

        restModeEvacuationEauUseeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModeEvacuationEauUsee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedModeEvacuationEauUsee))
            )
            .andExpect(status().isOk());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacuationEauUsee testModeEvacuationEauUsee = modeEvacuationEauUseeList.get(modeEvacuationEauUseeList.size() - 1);
        assertThat(testModeEvacuationEauUsee.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModeEvacuationEauUseeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, modeEvacuationEauUseeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeEvacuationEauUseeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeEvacuationEauUseeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    @Transactional
    void deleteModeEvacuationEauUsee() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);

        int databaseSizeBeforeDelete = modeEvacuationEauUseeRepository.findAll().size();

        // Delete the modeEvacuationEauUsee
        restModeEvacuationEauUseeMockMvc
            .perform(delete(ENTITY_API_URL_ID, modeEvacuationEauUsee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(1)).deleteById(modeEvacuationEauUsee.getId());
    }

    @Test
    @Transactional
    void searchModeEvacuationEauUsee() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        modeEvacuationEauUseeRepository.saveAndFlush(modeEvacuationEauUsee);
        when(
            mockModeEvacuationEauUseeSearchRepository.search(queryStringQuery("id:" + modeEvacuationEauUsee.getId()), PageRequest.of(0, 20))
        )
            .thenReturn(new PageImpl<>(Collections.singletonList(modeEvacuationEauUsee), PageRequest.of(0, 1), 1));

        // Search the modeEvacuationEauUsee
        restModeEvacuationEauUseeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + modeEvacuationEauUsee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modeEvacuationEauUsee.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }
}
