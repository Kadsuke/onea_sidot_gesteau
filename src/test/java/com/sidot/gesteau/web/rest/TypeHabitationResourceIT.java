package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import com.sidot.gesteau.domain.TypeHabitation;
import com.sidot.gesteau.repository.TypeHabitationRepository;
import com.sidot.gesteau.repository.search.TypeHabitationSearchRepository;
import com.sidot.gesteau.service.criteria.TypeHabitationCriteria;
import com.sidot.gesteau.service.dto.TypeHabitationDTO;
import com.sidot.gesteau.service.mapper.TypeHabitationMapper;
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
 * Integration tests for the {@link TypeHabitationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TypeHabitationResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-habitations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/type-habitations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypeHabitationRepository typeHabitationRepository;

    @Autowired
    private TypeHabitationMapper typeHabitationMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.TypeHabitationSearchRepositoryMockConfiguration
     */
    @Autowired
    private TypeHabitationSearchRepository mockTypeHabitationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypeHabitationMockMvc;

    private TypeHabitation typeHabitation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeHabitation createEntity(EntityManager em) {
        TypeHabitation typeHabitation = new TypeHabitation().libelle(DEFAULT_LIBELLE);
        return typeHabitation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeHabitation createUpdatedEntity(EntityManager em) {
        TypeHabitation typeHabitation = new TypeHabitation().libelle(UPDATED_LIBELLE);
        return typeHabitation;
    }

    @BeforeEach
    public void initTest() {
        typeHabitation = createEntity(em);
    }

    @Test
    @Transactional
    void createTypeHabitation() throws Exception {
        int databaseSizeBeforeCreate = typeHabitationRepository.findAll().size();
        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);
        restTypeHabitationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeCreate + 1);
        TypeHabitation testTypeHabitation = typeHabitationList.get(typeHabitationList.size() - 1);
        assertThat(testTypeHabitation.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(1)).save(testTypeHabitation);
    }

    @Test
    @Transactional
    void createTypeHabitationWithExistingId() throws Exception {
        // Create the TypeHabitation with an existing ID
        typeHabitation.setId(1L);
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        int databaseSizeBeforeCreate = typeHabitationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypeHabitationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeCreate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeHabitationRepository.findAll().size();
        // set the field null
        typeHabitation.setLibelle(null);

        // Create the TypeHabitation, which fails.
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        restTypeHabitationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isBadRequest());

        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTypeHabitations() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        // Get all the typeHabitationList
        restTypeHabitationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeHabitation.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getTypeHabitation() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        // Get the typeHabitation
        restTypeHabitationMockMvc
            .perform(get(ENTITY_API_URL_ID, typeHabitation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typeHabitation.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getTypeHabitationsByIdFiltering() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        Long id = typeHabitation.getId();

        defaultTypeHabitationShouldBeFound("id.equals=" + id);
        defaultTypeHabitationShouldNotBeFound("id.notEquals=" + id);

        defaultTypeHabitationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTypeHabitationShouldNotBeFound("id.greaterThan=" + id);

        defaultTypeHabitationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTypeHabitationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTypeHabitationsByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        // Get all the typeHabitationList where libelle equals to DEFAULT_LIBELLE
        defaultTypeHabitationShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the typeHabitationList where libelle equals to UPDATED_LIBELLE
        defaultTypeHabitationShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllTypeHabitationsByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        // Get all the typeHabitationList where libelle not equals to DEFAULT_LIBELLE
        defaultTypeHabitationShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the typeHabitationList where libelle not equals to UPDATED_LIBELLE
        defaultTypeHabitationShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllTypeHabitationsByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        // Get all the typeHabitationList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultTypeHabitationShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the typeHabitationList where libelle equals to UPDATED_LIBELLE
        defaultTypeHabitationShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllTypeHabitationsByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        // Get all the typeHabitationList where libelle is not null
        defaultTypeHabitationShouldBeFound("libelle.specified=true");

        // Get all the typeHabitationList where libelle is null
        defaultTypeHabitationShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllTypeHabitationsByLibelleContainsSomething() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        // Get all the typeHabitationList where libelle contains DEFAULT_LIBELLE
        defaultTypeHabitationShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the typeHabitationList where libelle contains UPDATED_LIBELLE
        defaultTypeHabitationShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllTypeHabitationsByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        // Get all the typeHabitationList where libelle does not contain DEFAULT_LIBELLE
        defaultTypeHabitationShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the typeHabitationList where libelle does not contain UPDATED_LIBELLE
        defaultTypeHabitationShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllTypeHabitationsByFicheSuiviOuvrageIsEqualToSomething() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);
        FicheSuiviOuvrage ficheSuiviOuvrage = FicheSuiviOuvrageResourceIT.createEntity(em);
        em.persist(ficheSuiviOuvrage);
        em.flush();
        typeHabitation.addFicheSuiviOuvrage(ficheSuiviOuvrage);
        typeHabitationRepository.saveAndFlush(typeHabitation);
        Long ficheSuiviOuvrageId = ficheSuiviOuvrage.getId();

        // Get all the typeHabitationList where ficheSuiviOuvrage equals to ficheSuiviOuvrageId
        defaultTypeHabitationShouldBeFound("ficheSuiviOuvrageId.equals=" + ficheSuiviOuvrageId);

        // Get all the typeHabitationList where ficheSuiviOuvrage equals to (ficheSuiviOuvrageId + 1)
        defaultTypeHabitationShouldNotBeFound("ficheSuiviOuvrageId.equals=" + (ficheSuiviOuvrageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTypeHabitationShouldBeFound(String filter) throws Exception {
        restTypeHabitationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeHabitation.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));

        // Check, that the count call also returns 1
        restTypeHabitationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTypeHabitationShouldNotBeFound(String filter) throws Exception {
        restTypeHabitationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTypeHabitationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTypeHabitation() throws Exception {
        // Get the typeHabitation
        restTypeHabitationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTypeHabitation() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();

        // Update the typeHabitation
        TypeHabitation updatedTypeHabitation = typeHabitationRepository.findById(typeHabitation.getId()).get();
        // Disconnect from session so that the updates on updatedTypeHabitation are not directly saved in db
        em.detach(updatedTypeHabitation);
        updatedTypeHabitation.libelle(UPDATED_LIBELLE);
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(updatedTypeHabitation);

        restTypeHabitationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeHabitationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isOk());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);
        TypeHabitation testTypeHabitation = typeHabitationList.get(typeHabitationList.size() - 1);
        assertThat(testTypeHabitation.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository).save(testTypeHabitation);
    }

    @Test
    @Transactional
    void putNonExistingTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeHabitationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeHabitationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeHabitationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeHabitationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    @Transactional
    void partialUpdateTypeHabitationWithPatch() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();

        // Update the typeHabitation using partial update
        TypeHabitation partialUpdatedTypeHabitation = new TypeHabitation();
        partialUpdatedTypeHabitation.setId(typeHabitation.getId());

        partialUpdatedTypeHabitation.libelle(UPDATED_LIBELLE);

        restTypeHabitationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeHabitation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeHabitation))
            )
            .andExpect(status().isOk());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);
        TypeHabitation testTypeHabitation = typeHabitationList.get(typeHabitationList.size() - 1);
        assertThat(testTypeHabitation.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateTypeHabitationWithPatch() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();

        // Update the typeHabitation using partial update
        TypeHabitation partialUpdatedTypeHabitation = new TypeHabitation();
        partialUpdatedTypeHabitation.setId(typeHabitation.getId());

        partialUpdatedTypeHabitation.libelle(UPDATED_LIBELLE);

        restTypeHabitationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeHabitation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeHabitation))
            )
            .andExpect(status().isOk());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);
        TypeHabitation testTypeHabitation = typeHabitationList.get(typeHabitationList.size() - 1);
        assertThat(testTypeHabitation.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeHabitationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typeHabitationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeHabitationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeHabitationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    @Transactional
    void deleteTypeHabitation() throws Exception {
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);

        int databaseSizeBeforeDelete = typeHabitationRepository.findAll().size();

        // Delete the typeHabitation
        restTypeHabitationMockMvc
            .perform(delete(ENTITY_API_URL_ID, typeHabitation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(1)).deleteById(typeHabitation.getId());
    }

    @Test
    @Transactional
    void searchTypeHabitation() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        typeHabitationRepository.saveAndFlush(typeHabitation);
        when(mockTypeHabitationSearchRepository.search(queryStringQuery("id:" + typeHabitation.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(typeHabitation), PageRequest.of(0, 1), 1));

        // Search the typeHabitation
        restTypeHabitationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + typeHabitation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeHabitation.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }
}
