package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.Annee;
import com.sidot.gesteau.domain.Centre;
import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import com.sidot.gesteau.domain.Prevision;
import com.sidot.gesteau.repository.PrevisionRepository;
import com.sidot.gesteau.repository.search.PrevisionSearchRepository;
import com.sidot.gesteau.service.criteria.PrevisionCriteria;
import com.sidot.gesteau.service.dto.PrevisionDTO;
import com.sidot.gesteau.service.mapper.PrevisionMapper;
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
 * Integration tests for the {@link PrevisionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PrevisionResourceIT {

    private static final Integer DEFAULT_NB_LATRINE = 1;
    private static final Integer UPDATED_NB_LATRINE = 2;
    private static final Integer SMALLER_NB_LATRINE = 1 - 1;

    private static final Integer DEFAULT_NB_PUISARD = 1;
    private static final Integer UPDATED_NB_PUISARD = 2;
    private static final Integer SMALLER_NB_PUISARD = 1 - 1;

    private static final Integer DEFAULT_NB_PUBLIC = 1;
    private static final Integer UPDATED_NB_PUBLIC = 2;
    private static final Integer SMALLER_NB_PUBLIC = 1 - 1;

    private static final Integer DEFAULT_NB_SCOLAIRE = 1;
    private static final Integer UPDATED_NB_SCOLAIRE = 2;
    private static final Integer SMALLER_NB_SCOLAIRE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/previsions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/previsions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PrevisionRepository previsionRepository;

    @Autowired
    private PrevisionMapper previsionMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.PrevisionSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrevisionSearchRepository mockPrevisionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrevisionMockMvc;

    private Prevision prevision;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prevision createEntity(EntityManager em) {
        Prevision prevision = new Prevision()
            .nbLatrine(DEFAULT_NB_LATRINE)
            .nbPuisard(DEFAULT_NB_PUISARD)
            .nbPublic(DEFAULT_NB_PUBLIC)
            .nbScolaire(DEFAULT_NB_SCOLAIRE);
        return prevision;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prevision createUpdatedEntity(EntityManager em) {
        Prevision prevision = new Prevision()
            .nbLatrine(UPDATED_NB_LATRINE)
            .nbPuisard(UPDATED_NB_PUISARD)
            .nbPublic(UPDATED_NB_PUBLIC)
            .nbScolaire(UPDATED_NB_SCOLAIRE);
        return prevision;
    }

    @BeforeEach
    public void initTest() {
        prevision = createEntity(em);
    }

    @Test
    @Transactional
    void createPrevision() throws Exception {
        int databaseSizeBeforeCreate = previsionRepository.findAll().size();
        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);
        restPrevisionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(previsionDTO)))
            .andExpect(status().isCreated());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeCreate + 1);
        Prevision testPrevision = previsionList.get(previsionList.size() - 1);
        assertThat(testPrevision.getNbLatrine()).isEqualTo(DEFAULT_NB_LATRINE);
        assertThat(testPrevision.getNbPuisard()).isEqualTo(DEFAULT_NB_PUISARD);
        assertThat(testPrevision.getNbPublic()).isEqualTo(DEFAULT_NB_PUBLIC);
        assertThat(testPrevision.getNbScolaire()).isEqualTo(DEFAULT_NB_SCOLAIRE);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(1)).save(testPrevision);
    }

    @Test
    @Transactional
    void createPrevisionWithExistingId() throws Exception {
        // Create the Prevision with an existing ID
        prevision.setId(1L);
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        int databaseSizeBeforeCreate = previsionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrevisionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(previsionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    @Transactional
    void checkNbLatrineIsRequired() throws Exception {
        int databaseSizeBeforeTest = previsionRepository.findAll().size();
        // set the field null
        prevision.setNbLatrine(null);

        // Create the Prevision, which fails.
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        restPrevisionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(previsionDTO)))
            .andExpect(status().isBadRequest());

        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNbPuisardIsRequired() throws Exception {
        int databaseSizeBeforeTest = previsionRepository.findAll().size();
        // set the field null
        prevision.setNbPuisard(null);

        // Create the Prevision, which fails.
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        restPrevisionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(previsionDTO)))
            .andExpect(status().isBadRequest());

        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNbPublicIsRequired() throws Exception {
        int databaseSizeBeforeTest = previsionRepository.findAll().size();
        // set the field null
        prevision.setNbPublic(null);

        // Create the Prevision, which fails.
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        restPrevisionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(previsionDTO)))
            .andExpect(status().isBadRequest());

        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNbScolaireIsRequired() throws Exception {
        int databaseSizeBeforeTest = previsionRepository.findAll().size();
        // set the field null
        prevision.setNbScolaire(null);

        // Create the Prevision, which fails.
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        restPrevisionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(previsionDTO)))
            .andExpect(status().isBadRequest());

        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPrevisions() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList
        restPrevisionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prevision.getId().intValue())))
            .andExpect(jsonPath("$.[*].nbLatrine").value(hasItem(DEFAULT_NB_LATRINE)))
            .andExpect(jsonPath("$.[*].nbPuisard").value(hasItem(DEFAULT_NB_PUISARD)))
            .andExpect(jsonPath("$.[*].nbPublic").value(hasItem(DEFAULT_NB_PUBLIC)))
            .andExpect(jsonPath("$.[*].nbScolaire").value(hasItem(DEFAULT_NB_SCOLAIRE)));
    }

    @Test
    @Transactional
    void getPrevision() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get the prevision
        restPrevisionMockMvc
            .perform(get(ENTITY_API_URL_ID, prevision.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prevision.getId().intValue()))
            .andExpect(jsonPath("$.nbLatrine").value(DEFAULT_NB_LATRINE))
            .andExpect(jsonPath("$.nbPuisard").value(DEFAULT_NB_PUISARD))
            .andExpect(jsonPath("$.nbPublic").value(DEFAULT_NB_PUBLIC))
            .andExpect(jsonPath("$.nbScolaire").value(DEFAULT_NB_SCOLAIRE));
    }

    @Test
    @Transactional
    void getPrevisionsByIdFiltering() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        Long id = prevision.getId();

        defaultPrevisionShouldBeFound("id.equals=" + id);
        defaultPrevisionShouldNotBeFound("id.notEquals=" + id);

        defaultPrevisionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrevisionShouldNotBeFound("id.greaterThan=" + id);

        defaultPrevisionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrevisionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbLatrineIsEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbLatrine equals to DEFAULT_NB_LATRINE
        defaultPrevisionShouldBeFound("nbLatrine.equals=" + DEFAULT_NB_LATRINE);

        // Get all the previsionList where nbLatrine equals to UPDATED_NB_LATRINE
        defaultPrevisionShouldNotBeFound("nbLatrine.equals=" + UPDATED_NB_LATRINE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbLatrineIsNotEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbLatrine not equals to DEFAULT_NB_LATRINE
        defaultPrevisionShouldNotBeFound("nbLatrine.notEquals=" + DEFAULT_NB_LATRINE);

        // Get all the previsionList where nbLatrine not equals to UPDATED_NB_LATRINE
        defaultPrevisionShouldBeFound("nbLatrine.notEquals=" + UPDATED_NB_LATRINE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbLatrineIsInShouldWork() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbLatrine in DEFAULT_NB_LATRINE or UPDATED_NB_LATRINE
        defaultPrevisionShouldBeFound("nbLatrine.in=" + DEFAULT_NB_LATRINE + "," + UPDATED_NB_LATRINE);

        // Get all the previsionList where nbLatrine equals to UPDATED_NB_LATRINE
        defaultPrevisionShouldNotBeFound("nbLatrine.in=" + UPDATED_NB_LATRINE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbLatrineIsNullOrNotNull() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbLatrine is not null
        defaultPrevisionShouldBeFound("nbLatrine.specified=true");

        // Get all the previsionList where nbLatrine is null
        defaultPrevisionShouldNotBeFound("nbLatrine.specified=false");
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbLatrineIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbLatrine is greater than or equal to DEFAULT_NB_LATRINE
        defaultPrevisionShouldBeFound("nbLatrine.greaterThanOrEqual=" + DEFAULT_NB_LATRINE);

        // Get all the previsionList where nbLatrine is greater than or equal to UPDATED_NB_LATRINE
        defaultPrevisionShouldNotBeFound("nbLatrine.greaterThanOrEqual=" + UPDATED_NB_LATRINE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbLatrineIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbLatrine is less than or equal to DEFAULT_NB_LATRINE
        defaultPrevisionShouldBeFound("nbLatrine.lessThanOrEqual=" + DEFAULT_NB_LATRINE);

        // Get all the previsionList where nbLatrine is less than or equal to SMALLER_NB_LATRINE
        defaultPrevisionShouldNotBeFound("nbLatrine.lessThanOrEqual=" + SMALLER_NB_LATRINE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbLatrineIsLessThanSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbLatrine is less than DEFAULT_NB_LATRINE
        defaultPrevisionShouldNotBeFound("nbLatrine.lessThan=" + DEFAULT_NB_LATRINE);

        // Get all the previsionList where nbLatrine is less than UPDATED_NB_LATRINE
        defaultPrevisionShouldBeFound("nbLatrine.lessThan=" + UPDATED_NB_LATRINE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbLatrineIsGreaterThanSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbLatrine is greater than DEFAULT_NB_LATRINE
        defaultPrevisionShouldNotBeFound("nbLatrine.greaterThan=" + DEFAULT_NB_LATRINE);

        // Get all the previsionList where nbLatrine is greater than SMALLER_NB_LATRINE
        defaultPrevisionShouldBeFound("nbLatrine.greaterThan=" + SMALLER_NB_LATRINE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPuisardIsEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPuisard equals to DEFAULT_NB_PUISARD
        defaultPrevisionShouldBeFound("nbPuisard.equals=" + DEFAULT_NB_PUISARD);

        // Get all the previsionList where nbPuisard equals to UPDATED_NB_PUISARD
        defaultPrevisionShouldNotBeFound("nbPuisard.equals=" + UPDATED_NB_PUISARD);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPuisardIsNotEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPuisard not equals to DEFAULT_NB_PUISARD
        defaultPrevisionShouldNotBeFound("nbPuisard.notEquals=" + DEFAULT_NB_PUISARD);

        // Get all the previsionList where nbPuisard not equals to UPDATED_NB_PUISARD
        defaultPrevisionShouldBeFound("nbPuisard.notEquals=" + UPDATED_NB_PUISARD);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPuisardIsInShouldWork() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPuisard in DEFAULT_NB_PUISARD or UPDATED_NB_PUISARD
        defaultPrevisionShouldBeFound("nbPuisard.in=" + DEFAULT_NB_PUISARD + "," + UPDATED_NB_PUISARD);

        // Get all the previsionList where nbPuisard equals to UPDATED_NB_PUISARD
        defaultPrevisionShouldNotBeFound("nbPuisard.in=" + UPDATED_NB_PUISARD);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPuisardIsNullOrNotNull() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPuisard is not null
        defaultPrevisionShouldBeFound("nbPuisard.specified=true");

        // Get all the previsionList where nbPuisard is null
        defaultPrevisionShouldNotBeFound("nbPuisard.specified=false");
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPuisardIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPuisard is greater than or equal to DEFAULT_NB_PUISARD
        defaultPrevisionShouldBeFound("nbPuisard.greaterThanOrEqual=" + DEFAULT_NB_PUISARD);

        // Get all the previsionList where nbPuisard is greater than or equal to UPDATED_NB_PUISARD
        defaultPrevisionShouldNotBeFound("nbPuisard.greaterThanOrEqual=" + UPDATED_NB_PUISARD);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPuisardIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPuisard is less than or equal to DEFAULT_NB_PUISARD
        defaultPrevisionShouldBeFound("nbPuisard.lessThanOrEqual=" + DEFAULT_NB_PUISARD);

        // Get all the previsionList where nbPuisard is less than or equal to SMALLER_NB_PUISARD
        defaultPrevisionShouldNotBeFound("nbPuisard.lessThanOrEqual=" + SMALLER_NB_PUISARD);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPuisardIsLessThanSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPuisard is less than DEFAULT_NB_PUISARD
        defaultPrevisionShouldNotBeFound("nbPuisard.lessThan=" + DEFAULT_NB_PUISARD);

        // Get all the previsionList where nbPuisard is less than UPDATED_NB_PUISARD
        defaultPrevisionShouldBeFound("nbPuisard.lessThan=" + UPDATED_NB_PUISARD);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPuisardIsGreaterThanSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPuisard is greater than DEFAULT_NB_PUISARD
        defaultPrevisionShouldNotBeFound("nbPuisard.greaterThan=" + DEFAULT_NB_PUISARD);

        // Get all the previsionList where nbPuisard is greater than SMALLER_NB_PUISARD
        defaultPrevisionShouldBeFound("nbPuisard.greaterThan=" + SMALLER_NB_PUISARD);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPublicIsEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPublic equals to DEFAULT_NB_PUBLIC
        defaultPrevisionShouldBeFound("nbPublic.equals=" + DEFAULT_NB_PUBLIC);

        // Get all the previsionList where nbPublic equals to UPDATED_NB_PUBLIC
        defaultPrevisionShouldNotBeFound("nbPublic.equals=" + UPDATED_NB_PUBLIC);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPublicIsNotEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPublic not equals to DEFAULT_NB_PUBLIC
        defaultPrevisionShouldNotBeFound("nbPublic.notEquals=" + DEFAULT_NB_PUBLIC);

        // Get all the previsionList where nbPublic not equals to UPDATED_NB_PUBLIC
        defaultPrevisionShouldBeFound("nbPublic.notEquals=" + UPDATED_NB_PUBLIC);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPublicIsInShouldWork() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPublic in DEFAULT_NB_PUBLIC or UPDATED_NB_PUBLIC
        defaultPrevisionShouldBeFound("nbPublic.in=" + DEFAULT_NB_PUBLIC + "," + UPDATED_NB_PUBLIC);

        // Get all the previsionList where nbPublic equals to UPDATED_NB_PUBLIC
        defaultPrevisionShouldNotBeFound("nbPublic.in=" + UPDATED_NB_PUBLIC);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPublicIsNullOrNotNull() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPublic is not null
        defaultPrevisionShouldBeFound("nbPublic.specified=true");

        // Get all the previsionList where nbPublic is null
        defaultPrevisionShouldNotBeFound("nbPublic.specified=false");
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPublicIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPublic is greater than or equal to DEFAULT_NB_PUBLIC
        defaultPrevisionShouldBeFound("nbPublic.greaterThanOrEqual=" + DEFAULT_NB_PUBLIC);

        // Get all the previsionList where nbPublic is greater than or equal to UPDATED_NB_PUBLIC
        defaultPrevisionShouldNotBeFound("nbPublic.greaterThanOrEqual=" + UPDATED_NB_PUBLIC);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPublicIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPublic is less than or equal to DEFAULT_NB_PUBLIC
        defaultPrevisionShouldBeFound("nbPublic.lessThanOrEqual=" + DEFAULT_NB_PUBLIC);

        // Get all the previsionList where nbPublic is less than or equal to SMALLER_NB_PUBLIC
        defaultPrevisionShouldNotBeFound("nbPublic.lessThanOrEqual=" + SMALLER_NB_PUBLIC);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPublicIsLessThanSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPublic is less than DEFAULT_NB_PUBLIC
        defaultPrevisionShouldNotBeFound("nbPublic.lessThan=" + DEFAULT_NB_PUBLIC);

        // Get all the previsionList where nbPublic is less than UPDATED_NB_PUBLIC
        defaultPrevisionShouldBeFound("nbPublic.lessThan=" + UPDATED_NB_PUBLIC);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbPublicIsGreaterThanSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbPublic is greater than DEFAULT_NB_PUBLIC
        defaultPrevisionShouldNotBeFound("nbPublic.greaterThan=" + DEFAULT_NB_PUBLIC);

        // Get all the previsionList where nbPublic is greater than SMALLER_NB_PUBLIC
        defaultPrevisionShouldBeFound("nbPublic.greaterThan=" + SMALLER_NB_PUBLIC);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbScolaireIsEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbScolaire equals to DEFAULT_NB_SCOLAIRE
        defaultPrevisionShouldBeFound("nbScolaire.equals=" + DEFAULT_NB_SCOLAIRE);

        // Get all the previsionList where nbScolaire equals to UPDATED_NB_SCOLAIRE
        defaultPrevisionShouldNotBeFound("nbScolaire.equals=" + UPDATED_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbScolaireIsNotEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbScolaire not equals to DEFAULT_NB_SCOLAIRE
        defaultPrevisionShouldNotBeFound("nbScolaire.notEquals=" + DEFAULT_NB_SCOLAIRE);

        // Get all the previsionList where nbScolaire not equals to UPDATED_NB_SCOLAIRE
        defaultPrevisionShouldBeFound("nbScolaire.notEquals=" + UPDATED_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbScolaireIsInShouldWork() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbScolaire in DEFAULT_NB_SCOLAIRE or UPDATED_NB_SCOLAIRE
        defaultPrevisionShouldBeFound("nbScolaire.in=" + DEFAULT_NB_SCOLAIRE + "," + UPDATED_NB_SCOLAIRE);

        // Get all the previsionList where nbScolaire equals to UPDATED_NB_SCOLAIRE
        defaultPrevisionShouldNotBeFound("nbScolaire.in=" + UPDATED_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbScolaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbScolaire is not null
        defaultPrevisionShouldBeFound("nbScolaire.specified=true");

        // Get all the previsionList where nbScolaire is null
        defaultPrevisionShouldNotBeFound("nbScolaire.specified=false");
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbScolaireIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbScolaire is greater than or equal to DEFAULT_NB_SCOLAIRE
        defaultPrevisionShouldBeFound("nbScolaire.greaterThanOrEqual=" + DEFAULT_NB_SCOLAIRE);

        // Get all the previsionList where nbScolaire is greater than or equal to UPDATED_NB_SCOLAIRE
        defaultPrevisionShouldNotBeFound("nbScolaire.greaterThanOrEqual=" + UPDATED_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbScolaireIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbScolaire is less than or equal to DEFAULT_NB_SCOLAIRE
        defaultPrevisionShouldBeFound("nbScolaire.lessThanOrEqual=" + DEFAULT_NB_SCOLAIRE);

        // Get all the previsionList where nbScolaire is less than or equal to SMALLER_NB_SCOLAIRE
        defaultPrevisionShouldNotBeFound("nbScolaire.lessThanOrEqual=" + SMALLER_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbScolaireIsLessThanSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbScolaire is less than DEFAULT_NB_SCOLAIRE
        defaultPrevisionShouldNotBeFound("nbScolaire.lessThan=" + DEFAULT_NB_SCOLAIRE);

        // Get all the previsionList where nbScolaire is less than UPDATED_NB_SCOLAIRE
        defaultPrevisionShouldBeFound("nbScolaire.lessThan=" + UPDATED_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByNbScolaireIsGreaterThanSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        // Get all the previsionList where nbScolaire is greater than DEFAULT_NB_SCOLAIRE
        defaultPrevisionShouldNotBeFound("nbScolaire.greaterThan=" + DEFAULT_NB_SCOLAIRE);

        // Get all the previsionList where nbScolaire is greater than SMALLER_NB_SCOLAIRE
        defaultPrevisionShouldBeFound("nbScolaire.greaterThan=" + SMALLER_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void getAllPrevisionsByAnneeIsEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);
        Annee annee = AnneeResourceIT.createEntity(em);
        em.persist(annee);
        em.flush();
        prevision.setAnnee(annee);
        previsionRepository.saveAndFlush(prevision);
        Long anneeId = annee.getId();

        // Get all the previsionList where annee equals to anneeId
        defaultPrevisionShouldBeFound("anneeId.equals=" + anneeId);

        // Get all the previsionList where annee equals to (anneeId + 1)
        defaultPrevisionShouldNotBeFound("anneeId.equals=" + (anneeId + 1));
    }

    @Test
    @Transactional
    void getAllPrevisionsByCentreIsEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);
        Centre centre = CentreResourceIT.createEntity(em);
        em.persist(centre);
        em.flush();
        prevision.setCentre(centre);
        previsionRepository.saveAndFlush(prevision);
        Long centreId = centre.getId();

        // Get all the previsionList where centre equals to centreId
        defaultPrevisionShouldBeFound("centreId.equals=" + centreId);

        // Get all the previsionList where centre equals to (centreId + 1)
        defaultPrevisionShouldNotBeFound("centreId.equals=" + (centreId + 1));
    }

    @Test
    @Transactional
    void getAllPrevisionsByFicheSuiviOuvrageIsEqualToSomething() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);
        FicheSuiviOuvrage ficheSuiviOuvrage = FicheSuiviOuvrageResourceIT.createEntity(em);
        em.persist(ficheSuiviOuvrage);
        em.flush();
        prevision.addFicheSuiviOuvrage(ficheSuiviOuvrage);
        previsionRepository.saveAndFlush(prevision);
        Long ficheSuiviOuvrageId = ficheSuiviOuvrage.getId();

        // Get all the previsionList where ficheSuiviOuvrage equals to ficheSuiviOuvrageId
        defaultPrevisionShouldBeFound("ficheSuiviOuvrageId.equals=" + ficheSuiviOuvrageId);

        // Get all the previsionList where ficheSuiviOuvrage equals to (ficheSuiviOuvrageId + 1)
        defaultPrevisionShouldNotBeFound("ficheSuiviOuvrageId.equals=" + (ficheSuiviOuvrageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrevisionShouldBeFound(String filter) throws Exception {
        restPrevisionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prevision.getId().intValue())))
            .andExpect(jsonPath("$.[*].nbLatrine").value(hasItem(DEFAULT_NB_LATRINE)))
            .andExpect(jsonPath("$.[*].nbPuisard").value(hasItem(DEFAULT_NB_PUISARD)))
            .andExpect(jsonPath("$.[*].nbPublic").value(hasItem(DEFAULT_NB_PUBLIC)))
            .andExpect(jsonPath("$.[*].nbScolaire").value(hasItem(DEFAULT_NB_SCOLAIRE)));

        // Check, that the count call also returns 1
        restPrevisionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrevisionShouldNotBeFound(String filter) throws Exception {
        restPrevisionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrevisionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPrevision() throws Exception {
        // Get the prevision
        restPrevisionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPrevision() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();

        // Update the prevision
        Prevision updatedPrevision = previsionRepository.findById(prevision.getId()).get();
        // Disconnect from session so that the updates on updatedPrevision are not directly saved in db
        em.detach(updatedPrevision);
        updatedPrevision
            .nbLatrine(UPDATED_NB_LATRINE)
            .nbPuisard(UPDATED_NB_PUISARD)
            .nbPublic(UPDATED_NB_PUBLIC)
            .nbScolaire(UPDATED_NB_SCOLAIRE);
        PrevisionDTO previsionDTO = previsionMapper.toDto(updatedPrevision);

        restPrevisionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, previsionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(previsionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);
        Prevision testPrevision = previsionList.get(previsionList.size() - 1);
        assertThat(testPrevision.getNbLatrine()).isEqualTo(UPDATED_NB_LATRINE);
        assertThat(testPrevision.getNbPuisard()).isEqualTo(UPDATED_NB_PUISARD);
        assertThat(testPrevision.getNbPublic()).isEqualTo(UPDATED_NB_PUBLIC);
        assertThat(testPrevision.getNbScolaire()).isEqualTo(UPDATED_NB_SCOLAIRE);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository).save(testPrevision);
    }

    @Test
    @Transactional
    void putNonExistingPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrevisionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, previsionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(previsionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    @Transactional
    void putWithIdMismatchPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrevisionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(previsionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrevisionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(previsionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    @Transactional
    void partialUpdatePrevisionWithPatch() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();

        // Update the prevision using partial update
        Prevision partialUpdatedPrevision = new Prevision();
        partialUpdatedPrevision.setId(prevision.getId());

        partialUpdatedPrevision
            .nbLatrine(UPDATED_NB_LATRINE)
            .nbPuisard(UPDATED_NB_PUISARD)
            .nbPublic(UPDATED_NB_PUBLIC)
            .nbScolaire(UPDATED_NB_SCOLAIRE);

        restPrevisionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrevision.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrevision))
            )
            .andExpect(status().isOk());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);
        Prevision testPrevision = previsionList.get(previsionList.size() - 1);
        assertThat(testPrevision.getNbLatrine()).isEqualTo(UPDATED_NB_LATRINE);
        assertThat(testPrevision.getNbPuisard()).isEqualTo(UPDATED_NB_PUISARD);
        assertThat(testPrevision.getNbPublic()).isEqualTo(UPDATED_NB_PUBLIC);
        assertThat(testPrevision.getNbScolaire()).isEqualTo(UPDATED_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void fullUpdatePrevisionWithPatch() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();

        // Update the prevision using partial update
        Prevision partialUpdatedPrevision = new Prevision();
        partialUpdatedPrevision.setId(prevision.getId());

        partialUpdatedPrevision
            .nbLatrine(UPDATED_NB_LATRINE)
            .nbPuisard(UPDATED_NB_PUISARD)
            .nbPublic(UPDATED_NB_PUBLIC)
            .nbScolaire(UPDATED_NB_SCOLAIRE);

        restPrevisionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrevision.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrevision))
            )
            .andExpect(status().isOk());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);
        Prevision testPrevision = previsionList.get(previsionList.size() - 1);
        assertThat(testPrevision.getNbLatrine()).isEqualTo(UPDATED_NB_LATRINE);
        assertThat(testPrevision.getNbPuisard()).isEqualTo(UPDATED_NB_PUISARD);
        assertThat(testPrevision.getNbPublic()).isEqualTo(UPDATED_NB_PUBLIC);
        assertThat(testPrevision.getNbScolaire()).isEqualTo(UPDATED_NB_SCOLAIRE);
    }

    @Test
    @Transactional
    void patchNonExistingPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrevisionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, previsionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(previsionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrevisionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(previsionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrevisionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(previsionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    @Transactional
    void deletePrevision() throws Exception {
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);

        int databaseSizeBeforeDelete = previsionRepository.findAll().size();

        // Delete the prevision
        restPrevisionMockMvc
            .perform(delete(ENTITY_API_URL_ID, prevision.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Prevision> previsionList = previsionRepository.findAll();
        assertThat(previsionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(1)).deleteById(prevision.getId());
    }

    @Test
    @Transactional
    void searchPrevision() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        previsionRepository.saveAndFlush(prevision);
        when(mockPrevisionSearchRepository.search(queryStringQuery("id:" + prevision.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(prevision), PageRequest.of(0, 1), 1));

        // Search the prevision
        restPrevisionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + prevision.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prevision.getId().intValue())))
            .andExpect(jsonPath("$.[*].nbLatrine").value(hasItem(DEFAULT_NB_LATRINE)))
            .andExpect(jsonPath("$.[*].nbPuisard").value(hasItem(DEFAULT_NB_PUISARD)))
            .andExpect(jsonPath("$.[*].nbPublic").value(hasItem(DEFAULT_NB_PUBLIC)))
            .andExpect(jsonPath("$.[*].nbScolaire").value(hasItem(DEFAULT_NB_SCOLAIRE)));
    }
}
