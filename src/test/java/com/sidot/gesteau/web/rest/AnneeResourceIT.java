package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.Annee;
import com.sidot.gesteau.domain.Prevision;
import com.sidot.gesteau.repository.AnneeRepository;
import com.sidot.gesteau.repository.search.AnneeSearchRepository;
import com.sidot.gesteau.service.criteria.AnneeCriteria;
import com.sidot.gesteau.service.dto.AnneeDTO;
import com.sidot.gesteau.service.mapper.AnneeMapper;
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
 * Integration tests for the {@link AnneeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AnneeResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/annees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/annees";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AnneeRepository anneeRepository;

    @Autowired
    private AnneeMapper anneeMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.AnneeSearchRepositoryMockConfiguration
     */
    @Autowired
    private AnneeSearchRepository mockAnneeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAnneeMockMvc;

    private Annee annee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Annee createEntity(EntityManager em) {
        Annee annee = new Annee().libelle(DEFAULT_LIBELLE);
        return annee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Annee createUpdatedEntity(EntityManager em) {
        Annee annee = new Annee().libelle(UPDATED_LIBELLE);
        return annee;
    }

    @BeforeEach
    public void initTest() {
        annee = createEntity(em);
    }

    @Test
    @Transactional
    void createAnnee() throws Exception {
        int databaseSizeBeforeCreate = anneeRepository.findAll().size();
        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);
        restAnneeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(anneeDTO)))
            .andExpect(status().isCreated());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeCreate + 1);
        Annee testAnnee = anneeList.get(anneeList.size() - 1);
        assertThat(testAnnee.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(1)).save(testAnnee);
    }

    @Test
    @Transactional
    void createAnneeWithExistingId() throws Exception {
        // Create the Annee with an existing ID
        annee.setId(1L);
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        int databaseSizeBeforeCreate = anneeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnneeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(anneeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = anneeRepository.findAll().size();
        // set the field null
        annee.setLibelle(null);

        // Create the Annee, which fails.
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        restAnneeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(anneeDTO)))
            .andExpect(status().isBadRequest());

        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAnnees() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        // Get all the anneeList
        restAnneeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(annee.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getAnnee() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        // Get the annee
        restAnneeMockMvc
            .perform(get(ENTITY_API_URL_ID, annee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(annee.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getAnneesByIdFiltering() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        Long id = annee.getId();

        defaultAnneeShouldBeFound("id.equals=" + id);
        defaultAnneeShouldNotBeFound("id.notEquals=" + id);

        defaultAnneeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAnneeShouldNotBeFound("id.greaterThan=" + id);

        defaultAnneeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAnneeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAnneesByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        // Get all the anneeList where libelle equals to DEFAULT_LIBELLE
        defaultAnneeShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the anneeList where libelle equals to UPDATED_LIBELLE
        defaultAnneeShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllAnneesByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        // Get all the anneeList where libelle not equals to DEFAULT_LIBELLE
        defaultAnneeShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the anneeList where libelle not equals to UPDATED_LIBELLE
        defaultAnneeShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllAnneesByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        // Get all the anneeList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultAnneeShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the anneeList where libelle equals to UPDATED_LIBELLE
        defaultAnneeShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllAnneesByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        // Get all the anneeList where libelle is not null
        defaultAnneeShouldBeFound("libelle.specified=true");

        // Get all the anneeList where libelle is null
        defaultAnneeShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllAnneesByLibelleContainsSomething() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        // Get all the anneeList where libelle contains DEFAULT_LIBELLE
        defaultAnneeShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the anneeList where libelle contains UPDATED_LIBELLE
        defaultAnneeShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllAnneesByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        // Get all the anneeList where libelle does not contain DEFAULT_LIBELLE
        defaultAnneeShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the anneeList where libelle does not contain UPDATED_LIBELLE
        defaultAnneeShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllAnneesByPrevisionIsEqualToSomething() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);
        Prevision prevision = PrevisionResourceIT.createEntity(em);
        em.persist(prevision);
        em.flush();
        annee.setPrevision(prevision);
        prevision.setAnnee(annee);
        anneeRepository.saveAndFlush(annee);
        Long previsionId = prevision.getId();

        // Get all the anneeList where prevision equals to previsionId
        defaultAnneeShouldBeFound("previsionId.equals=" + previsionId);

        // Get all the anneeList where prevision equals to (previsionId + 1)
        defaultAnneeShouldNotBeFound("previsionId.equals=" + (previsionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAnneeShouldBeFound(String filter) throws Exception {
        restAnneeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(annee.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));

        // Check, that the count call also returns 1
        restAnneeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAnneeShouldNotBeFound(String filter) throws Exception {
        restAnneeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAnneeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAnnee() throws Exception {
        // Get the annee
        restAnneeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAnnee() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();

        // Update the annee
        Annee updatedAnnee = anneeRepository.findById(annee.getId()).get();
        // Disconnect from session so that the updates on updatedAnnee are not directly saved in db
        em.detach(updatedAnnee);
        updatedAnnee.libelle(UPDATED_LIBELLE);
        AnneeDTO anneeDTO = anneeMapper.toDto(updatedAnnee);

        restAnneeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, anneeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(anneeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);
        Annee testAnnee = anneeList.get(anneeList.size() - 1);
        assertThat(testAnnee.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository).save(testAnnee);
    }

    @Test
    @Transactional
    void putNonExistingAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnneeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, anneeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(anneeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    @Transactional
    void putWithIdMismatchAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnneeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(anneeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnneeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(anneeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    @Transactional
    void partialUpdateAnneeWithPatch() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();

        // Update the annee using partial update
        Annee partialUpdatedAnnee = new Annee();
        partialUpdatedAnnee.setId(annee.getId());

        partialUpdatedAnnee.libelle(UPDATED_LIBELLE);

        restAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnnee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnnee))
            )
            .andExpect(status().isOk());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);
        Annee testAnnee = anneeList.get(anneeList.size() - 1);
        assertThat(testAnnee.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateAnneeWithPatch() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();

        // Update the annee using partial update
        Annee partialUpdatedAnnee = new Annee();
        partialUpdatedAnnee.setId(annee.getId());

        partialUpdatedAnnee.libelle(UPDATED_LIBELLE);

        restAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnnee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnnee))
            )
            .andExpect(status().isOk());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);
        Annee testAnnee = anneeList.get(anneeList.size() - 1);
        assertThat(testAnnee.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, anneeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(anneeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(anneeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnneeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(anneeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    @Transactional
    void deleteAnnee() throws Exception {
        // Initialize the database
        anneeRepository.saveAndFlush(annee);

        int databaseSizeBeforeDelete = anneeRepository.findAll().size();

        // Delete the annee
        restAnneeMockMvc
            .perform(delete(ENTITY_API_URL_ID, annee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Annee> anneeList = anneeRepository.findAll();
        assertThat(anneeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(1)).deleteById(annee.getId());
    }

    @Test
    @Transactional
    void searchAnnee() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        anneeRepository.saveAndFlush(annee);
        when(mockAnneeSearchRepository.search(queryStringQuery("id:" + annee.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(annee), PageRequest.of(0, 1), 1));

        // Search the annee
        restAnneeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + annee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(annee.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }
}
