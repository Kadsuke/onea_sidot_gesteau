package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.CentreRegroupement;
import com.sidot.gesteau.domain.DirectionRegionale;
import com.sidot.gesteau.repository.DirectionRegionaleRepository;
import com.sidot.gesteau.repository.search.DirectionRegionaleSearchRepository;
import com.sidot.gesteau.service.criteria.DirectionRegionaleCriteria;
import com.sidot.gesteau.service.dto.DirectionRegionaleDTO;
import com.sidot.gesteau.service.mapper.DirectionRegionaleMapper;
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
 * Integration tests for the {@link DirectionRegionaleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DirectionRegionaleResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSABLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/direction-regionales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/direction-regionales";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DirectionRegionaleRepository directionRegionaleRepository;

    @Autowired
    private DirectionRegionaleMapper directionRegionaleMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.DirectionRegionaleSearchRepositoryMockConfiguration
     */
    @Autowired
    private DirectionRegionaleSearchRepository mockDirectionRegionaleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDirectionRegionaleMockMvc;

    private DirectionRegionale directionRegionale;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DirectionRegionale createEntity(EntityManager em) {
        DirectionRegionale directionRegionale = new DirectionRegionale()
            .libelle(DEFAULT_LIBELLE)
            .responsable(DEFAULT_RESPONSABLE)
            .contact(DEFAULT_CONTACT);
        return directionRegionale;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DirectionRegionale createUpdatedEntity(EntityManager em) {
        DirectionRegionale directionRegionale = new DirectionRegionale()
            .libelle(UPDATED_LIBELLE)
            .responsable(UPDATED_RESPONSABLE)
            .contact(UPDATED_CONTACT);
        return directionRegionale;
    }

    @BeforeEach
    public void initTest() {
        directionRegionale = createEntity(em);
    }

    @Test
    @Transactional
    void createDirectionRegionale() throws Exception {
        int databaseSizeBeforeCreate = directionRegionaleRepository.findAll().size();
        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);
        restDirectionRegionaleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isCreated());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeCreate + 1);
        DirectionRegionale testDirectionRegionale = directionRegionaleList.get(directionRegionaleList.size() - 1);
        assertThat(testDirectionRegionale.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testDirectionRegionale.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testDirectionRegionale.getContact()).isEqualTo(DEFAULT_CONTACT);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(1)).save(testDirectionRegionale);
    }

    @Test
    @Transactional
    void createDirectionRegionaleWithExistingId() throws Exception {
        // Create the DirectionRegionale with an existing ID
        directionRegionale.setId(1L);
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        int databaseSizeBeforeCreate = directionRegionaleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDirectionRegionaleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeCreate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = directionRegionaleRepository.findAll().size();
        // set the field null
        directionRegionale.setLibelle(null);

        // Create the DirectionRegionale, which fails.
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        restDirectionRegionaleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isBadRequest());

        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkResponsableIsRequired() throws Exception {
        int databaseSizeBeforeTest = directionRegionaleRepository.findAll().size();
        // set the field null
        directionRegionale.setResponsable(null);

        // Create the DirectionRegionale, which fails.
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        restDirectionRegionaleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isBadRequest());

        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContactIsRequired() throws Exception {
        int databaseSizeBeforeTest = directionRegionaleRepository.findAll().size();
        // set the field null
        directionRegionale.setContact(null);

        // Create the DirectionRegionale, which fails.
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        restDirectionRegionaleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isBadRequest());

        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDirectionRegionales() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList
        restDirectionRegionaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(directionRegionale.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].responsable").value(hasItem(DEFAULT_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)));
    }

    @Test
    @Transactional
    void getDirectionRegionale() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get the directionRegionale
        restDirectionRegionaleMockMvc
            .perform(get(ENTITY_API_URL_ID, directionRegionale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(directionRegionale.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.responsable").value(DEFAULT_RESPONSABLE))
            .andExpect(jsonPath("$.contact").value(DEFAULT_CONTACT));
    }

    @Test
    @Transactional
    void getDirectionRegionalesByIdFiltering() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        Long id = directionRegionale.getId();

        defaultDirectionRegionaleShouldBeFound("id.equals=" + id);
        defaultDirectionRegionaleShouldNotBeFound("id.notEquals=" + id);

        defaultDirectionRegionaleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDirectionRegionaleShouldNotBeFound("id.greaterThan=" + id);

        defaultDirectionRegionaleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDirectionRegionaleShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where libelle equals to DEFAULT_LIBELLE
        defaultDirectionRegionaleShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the directionRegionaleList where libelle equals to UPDATED_LIBELLE
        defaultDirectionRegionaleShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where libelle not equals to DEFAULT_LIBELLE
        defaultDirectionRegionaleShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the directionRegionaleList where libelle not equals to UPDATED_LIBELLE
        defaultDirectionRegionaleShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultDirectionRegionaleShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the directionRegionaleList where libelle equals to UPDATED_LIBELLE
        defaultDirectionRegionaleShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where libelle is not null
        defaultDirectionRegionaleShouldBeFound("libelle.specified=true");

        // Get all the directionRegionaleList where libelle is null
        defaultDirectionRegionaleShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByLibelleContainsSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where libelle contains DEFAULT_LIBELLE
        defaultDirectionRegionaleShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the directionRegionaleList where libelle contains UPDATED_LIBELLE
        defaultDirectionRegionaleShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where libelle does not contain DEFAULT_LIBELLE
        defaultDirectionRegionaleShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the directionRegionaleList where libelle does not contain UPDATED_LIBELLE
        defaultDirectionRegionaleShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByResponsableIsEqualToSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where responsable equals to DEFAULT_RESPONSABLE
        defaultDirectionRegionaleShouldBeFound("responsable.equals=" + DEFAULT_RESPONSABLE);

        // Get all the directionRegionaleList where responsable equals to UPDATED_RESPONSABLE
        defaultDirectionRegionaleShouldNotBeFound("responsable.equals=" + UPDATED_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByResponsableIsNotEqualToSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where responsable not equals to DEFAULT_RESPONSABLE
        defaultDirectionRegionaleShouldNotBeFound("responsable.notEquals=" + DEFAULT_RESPONSABLE);

        // Get all the directionRegionaleList where responsable not equals to UPDATED_RESPONSABLE
        defaultDirectionRegionaleShouldBeFound("responsable.notEquals=" + UPDATED_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByResponsableIsInShouldWork() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where responsable in DEFAULT_RESPONSABLE or UPDATED_RESPONSABLE
        defaultDirectionRegionaleShouldBeFound("responsable.in=" + DEFAULT_RESPONSABLE + "," + UPDATED_RESPONSABLE);

        // Get all the directionRegionaleList where responsable equals to UPDATED_RESPONSABLE
        defaultDirectionRegionaleShouldNotBeFound("responsable.in=" + UPDATED_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByResponsableIsNullOrNotNull() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where responsable is not null
        defaultDirectionRegionaleShouldBeFound("responsable.specified=true");

        // Get all the directionRegionaleList where responsable is null
        defaultDirectionRegionaleShouldNotBeFound("responsable.specified=false");
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByResponsableContainsSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where responsable contains DEFAULT_RESPONSABLE
        defaultDirectionRegionaleShouldBeFound("responsable.contains=" + DEFAULT_RESPONSABLE);

        // Get all the directionRegionaleList where responsable contains UPDATED_RESPONSABLE
        defaultDirectionRegionaleShouldNotBeFound("responsable.contains=" + UPDATED_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByResponsableNotContainsSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where responsable does not contain DEFAULT_RESPONSABLE
        defaultDirectionRegionaleShouldNotBeFound("responsable.doesNotContain=" + DEFAULT_RESPONSABLE);

        // Get all the directionRegionaleList where responsable does not contain UPDATED_RESPONSABLE
        defaultDirectionRegionaleShouldBeFound("responsable.doesNotContain=" + UPDATED_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByContactIsEqualToSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where contact equals to DEFAULT_CONTACT
        defaultDirectionRegionaleShouldBeFound("contact.equals=" + DEFAULT_CONTACT);

        // Get all the directionRegionaleList where contact equals to UPDATED_CONTACT
        defaultDirectionRegionaleShouldNotBeFound("contact.equals=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByContactIsNotEqualToSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where contact not equals to DEFAULT_CONTACT
        defaultDirectionRegionaleShouldNotBeFound("contact.notEquals=" + DEFAULT_CONTACT);

        // Get all the directionRegionaleList where contact not equals to UPDATED_CONTACT
        defaultDirectionRegionaleShouldBeFound("contact.notEquals=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByContactIsInShouldWork() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where contact in DEFAULT_CONTACT or UPDATED_CONTACT
        defaultDirectionRegionaleShouldBeFound("contact.in=" + DEFAULT_CONTACT + "," + UPDATED_CONTACT);

        // Get all the directionRegionaleList where contact equals to UPDATED_CONTACT
        defaultDirectionRegionaleShouldNotBeFound("contact.in=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByContactIsNullOrNotNull() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where contact is not null
        defaultDirectionRegionaleShouldBeFound("contact.specified=true");

        // Get all the directionRegionaleList where contact is null
        defaultDirectionRegionaleShouldNotBeFound("contact.specified=false");
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByContactContainsSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where contact contains DEFAULT_CONTACT
        defaultDirectionRegionaleShouldBeFound("contact.contains=" + DEFAULT_CONTACT);

        // Get all the directionRegionaleList where contact contains UPDATED_CONTACT
        defaultDirectionRegionaleShouldNotBeFound("contact.contains=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByContactNotContainsSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        // Get all the directionRegionaleList where contact does not contain DEFAULT_CONTACT
        defaultDirectionRegionaleShouldNotBeFound("contact.doesNotContain=" + DEFAULT_CONTACT);

        // Get all the directionRegionaleList where contact does not contain UPDATED_CONTACT
        defaultDirectionRegionaleShouldBeFound("contact.doesNotContain=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllDirectionRegionalesByCentreRegroupementIsEqualToSomething() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);
        CentreRegroupement centreRegroupement = CentreRegroupementResourceIT.createEntity(em);
        em.persist(centreRegroupement);
        em.flush();
        directionRegionale.addCentreRegroupement(centreRegroupement);
        directionRegionaleRepository.saveAndFlush(directionRegionale);
        Long centreRegroupementId = centreRegroupement.getId();

        // Get all the directionRegionaleList where centreRegroupement equals to centreRegroupementId
        defaultDirectionRegionaleShouldBeFound("centreRegroupementId.equals=" + centreRegroupementId);

        // Get all the directionRegionaleList where centreRegroupement equals to (centreRegroupementId + 1)
        defaultDirectionRegionaleShouldNotBeFound("centreRegroupementId.equals=" + (centreRegroupementId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDirectionRegionaleShouldBeFound(String filter) throws Exception {
        restDirectionRegionaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(directionRegionale.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].responsable").value(hasItem(DEFAULT_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)));

        // Check, that the count call also returns 1
        restDirectionRegionaleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDirectionRegionaleShouldNotBeFound(String filter) throws Exception {
        restDirectionRegionaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDirectionRegionaleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDirectionRegionale() throws Exception {
        // Get the directionRegionale
        restDirectionRegionaleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDirectionRegionale() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();

        // Update the directionRegionale
        DirectionRegionale updatedDirectionRegionale = directionRegionaleRepository.findById(directionRegionale.getId()).get();
        // Disconnect from session so that the updates on updatedDirectionRegionale are not directly saved in db
        em.detach(updatedDirectionRegionale);
        updatedDirectionRegionale.libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(updatedDirectionRegionale);

        restDirectionRegionaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, directionRegionaleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isOk());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);
        DirectionRegionale testDirectionRegionale = directionRegionaleList.get(directionRegionaleList.size() - 1);
        assertThat(testDirectionRegionale.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testDirectionRegionale.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testDirectionRegionale.getContact()).isEqualTo(UPDATED_CONTACT);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository).save(testDirectionRegionale);
    }

    @Test
    @Transactional
    void putNonExistingDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectionRegionaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, directionRegionaleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    @Transactional
    void putWithIdMismatchDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectionRegionaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectionRegionaleMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    @Transactional
    void partialUpdateDirectionRegionaleWithPatch() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();

        // Update the directionRegionale using partial update
        DirectionRegionale partialUpdatedDirectionRegionale = new DirectionRegionale();
        partialUpdatedDirectionRegionale.setId(directionRegionale.getId());

        restDirectionRegionaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirectionRegionale.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirectionRegionale))
            )
            .andExpect(status().isOk());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);
        DirectionRegionale testDirectionRegionale = directionRegionaleList.get(directionRegionaleList.size() - 1);
        assertThat(testDirectionRegionale.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testDirectionRegionale.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testDirectionRegionale.getContact()).isEqualTo(DEFAULT_CONTACT);
    }

    @Test
    @Transactional
    void fullUpdateDirectionRegionaleWithPatch() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();

        // Update the directionRegionale using partial update
        DirectionRegionale partialUpdatedDirectionRegionale = new DirectionRegionale();
        partialUpdatedDirectionRegionale.setId(directionRegionale.getId());

        partialUpdatedDirectionRegionale.libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);

        restDirectionRegionaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirectionRegionale.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirectionRegionale))
            )
            .andExpect(status().isOk());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);
        DirectionRegionale testDirectionRegionale = directionRegionaleList.get(directionRegionaleList.size() - 1);
        assertThat(testDirectionRegionale.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testDirectionRegionale.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testDirectionRegionale.getContact()).isEqualTo(UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void patchNonExistingDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectionRegionaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, directionRegionaleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectionRegionaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectionRegionaleMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    @Transactional
    void deleteDirectionRegionale() throws Exception {
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);

        int databaseSizeBeforeDelete = directionRegionaleRepository.findAll().size();

        // Delete the directionRegionale
        restDirectionRegionaleMockMvc
            .perform(delete(ENTITY_API_URL_ID, directionRegionale.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(1)).deleteById(directionRegionale.getId());
    }

    @Test
    @Transactional
    void searchDirectionRegionale() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        directionRegionaleRepository.saveAndFlush(directionRegionale);
        when(mockDirectionRegionaleSearchRepository.search(queryStringQuery("id:" + directionRegionale.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(directionRegionale), PageRequest.of(0, 1), 1));

        // Search the directionRegionale
        restDirectionRegionaleMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + directionRegionale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(directionRegionale.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].responsable").value(hasItem(DEFAULT_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)));
    }
}
