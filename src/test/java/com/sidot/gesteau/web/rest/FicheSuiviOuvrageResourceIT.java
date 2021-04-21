package com.sidot.gesteau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sidot.gesteau.IntegrationTest;
import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import com.sidot.gesteau.domain.Macon;
import com.sidot.gesteau.domain.ModeEvacExcreta;
import com.sidot.gesteau.domain.ModeEvacuationEauUsee;
import com.sidot.gesteau.domain.NatureOuvrage;
import com.sidot.gesteau.domain.Prefabricant;
import com.sidot.gesteau.domain.Prevision;
import com.sidot.gesteau.domain.SourceApprovEp;
import com.sidot.gesteau.domain.TypeHabitation;
import com.sidot.gesteau.repository.FicheSuiviOuvrageRepository;
import com.sidot.gesteau.repository.search.FicheSuiviOuvrageSearchRepository;
import com.sidot.gesteau.service.criteria.FicheSuiviOuvrageCriteria;
import com.sidot.gesteau.service.dto.FicheSuiviOuvrageDTO;
import com.sidot.gesteau.service.mapper.FicheSuiviOuvrageMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link FicheSuiviOuvrageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FicheSuiviOuvrageResourceIT {

    private static final String DEFAULT_PRJ_APPUIS = "AAAAAAAAAA";
    private static final String UPDATED_PRJ_APPUIS = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_BENEF = "AAAAAAAAAA";
    private static final String UPDATED_NOM_BENEF = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM_BENEF = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM_BENEF = "BBBBBBBBBB";

    private static final String DEFAULT_PROFESSION_BENEF = "AAAAAAAAAA";
    private static final String UPDATED_PROFESSION_BENEF = "BBBBBBBBBB";

    private static final Long DEFAULT_NB_USAGERS = 1L;
    private static final Long UPDATED_NB_USAGERS = 2L;
    private static final Long SMALLER_NB_USAGERS = 1L - 1L;

    private static final String DEFAULT_CONTACTS = "AAAAAAAAAA";
    private static final String UPDATED_CONTACTS = "BBBBBBBBBB";

    private static final Float DEFAULT_LONGITUDE = 1F;
    private static final Float UPDATED_LONGITUDE = 2F;
    private static final Float SMALLER_LONGITUDE = 1F - 1F;

    private static final Float DEFAULT_LATITUDE = 1F;
    private static final Float UPDATED_LATITUDE = 2F;
    private static final Float SMALLER_LATITUDE = 1F - 1F;

    private static final Instant DEFAULT_DATE_REMISE_DEVIS = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_REMISE_DEVIS = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_DEBUT_TRAVAUX = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DEBUT_TRAVAUX = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_FIN_TRAVAUX = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_FIN_TRAVAUX = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RUE = "AAAAAAAAAA";
    private static final String UPDATED_RUE = "BBBBBBBBBB";

    private static final Integer DEFAULT_PORTE = 1;
    private static final Integer UPDATED_PORTE = 2;
    private static final Integer SMALLER_PORTE = 1 - 1;

    private static final String DEFAULT_COUT_MENAGE = "AAAAAAAAAA";
    private static final String UPDATED_COUT_MENAGE = "BBBBBBBBBB";

    private static final Integer DEFAULT_SUBV_ONEA = 1;
    private static final Integer UPDATED_SUBV_ONEA = 2;
    private static final Integer SMALLER_SUBV_ONEA = 1 - 1;

    private static final Integer DEFAULT_SUBV_PROJET = 1;
    private static final Integer UPDATED_SUBV_PROJET = 2;
    private static final Integer SMALLER_SUBV_PROJET = 1 - 1;

    private static final Integer DEFAULT_AUTRE_SUBV = 1;
    private static final Integer UPDATED_AUTRE_SUBV = 2;
    private static final Integer SMALLER_AUTRE_SUBV = 1 - 1;

    private static final Integer DEFAULT_TOLES = 1;
    private static final Integer UPDATED_TOLES = 2;
    private static final Integer SMALLER_TOLES = 1 - 1;

    private static final String DEFAULT_ANIMATEUR = "AAAAAAAAAA";
    private static final String UPDATED_ANIMATEUR = "BBBBBBBBBB";

    private static final String DEFAULT_SUPERVISEUR = "AAAAAAAAAA";
    private static final String UPDATED_SUPERVISEUR = "BBBBBBBBBB";

    private static final String DEFAULT_CONTROLEUR = "AAAAAAAAAA";
    private static final String UPDATED_CONTROLEUR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/fiche-suivi-ouvrages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/fiche-suivi-ouvrages";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FicheSuiviOuvrageRepository ficheSuiviOuvrageRepository;

    @Autowired
    private FicheSuiviOuvrageMapper ficheSuiviOuvrageMapper;

    /**
     * This repository is mocked in the com.sidot.gesteau.repository.search test package.
     *
     * @see com.sidot.gesteau.repository.search.FicheSuiviOuvrageSearchRepositoryMockConfiguration
     */
    @Autowired
    private FicheSuiviOuvrageSearchRepository mockFicheSuiviOuvrageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFicheSuiviOuvrageMockMvc;

    private FicheSuiviOuvrage ficheSuiviOuvrage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FicheSuiviOuvrage createEntity(EntityManager em) {
        FicheSuiviOuvrage ficheSuiviOuvrage = new FicheSuiviOuvrage()
            .prjAppuis(DEFAULT_PRJ_APPUIS)
            .nomBenef(DEFAULT_NOM_BENEF)
            .prenomBenef(DEFAULT_PRENOM_BENEF)
            .professionBenef(DEFAULT_PROFESSION_BENEF)
            .nbUsagers(DEFAULT_NB_USAGERS)
            .contacts(DEFAULT_CONTACTS)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .dateRemiseDevis(DEFAULT_DATE_REMISE_DEVIS)
            .dateDebutTravaux(DEFAULT_DATE_DEBUT_TRAVAUX)
            .dateFinTravaux(DEFAULT_DATE_FIN_TRAVAUX)
            .rue(DEFAULT_RUE)
            .porte(DEFAULT_PORTE)
            .coutMenage(DEFAULT_COUT_MENAGE)
            .subvOnea(DEFAULT_SUBV_ONEA)
            .subvProjet(DEFAULT_SUBV_PROJET)
            .autreSubv(DEFAULT_AUTRE_SUBV)
            .toles(DEFAULT_TOLES)
            .animateur(DEFAULT_ANIMATEUR)
            .superviseur(DEFAULT_SUPERVISEUR)
            .controleur(DEFAULT_CONTROLEUR);
        return ficheSuiviOuvrage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FicheSuiviOuvrage createUpdatedEntity(EntityManager em) {
        FicheSuiviOuvrage ficheSuiviOuvrage = new FicheSuiviOuvrage()
            .prjAppuis(UPDATED_PRJ_APPUIS)
            .nomBenef(UPDATED_NOM_BENEF)
            .prenomBenef(UPDATED_PRENOM_BENEF)
            .professionBenef(UPDATED_PROFESSION_BENEF)
            .nbUsagers(UPDATED_NB_USAGERS)
            .contacts(UPDATED_CONTACTS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .dateRemiseDevis(UPDATED_DATE_REMISE_DEVIS)
            .dateDebutTravaux(UPDATED_DATE_DEBUT_TRAVAUX)
            .dateFinTravaux(UPDATED_DATE_FIN_TRAVAUX)
            .rue(UPDATED_RUE)
            .porte(UPDATED_PORTE)
            .coutMenage(UPDATED_COUT_MENAGE)
            .subvOnea(UPDATED_SUBV_ONEA)
            .subvProjet(UPDATED_SUBV_PROJET)
            .autreSubv(UPDATED_AUTRE_SUBV)
            .toles(UPDATED_TOLES)
            .animateur(UPDATED_ANIMATEUR)
            .superviseur(UPDATED_SUPERVISEUR)
            .controleur(UPDATED_CONTROLEUR);
        return ficheSuiviOuvrage;
    }

    @BeforeEach
    public void initTest() {
        ficheSuiviOuvrage = createEntity(em);
    }

    @Test
    @Transactional
    void createFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeCreate = ficheSuiviOuvrageRepository.findAll().size();
        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);
        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isCreated());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeCreate + 1);
        FicheSuiviOuvrage testFicheSuiviOuvrage = ficheSuiviOuvrageList.get(ficheSuiviOuvrageList.size() - 1);
        assertThat(testFicheSuiviOuvrage.getPrjAppuis()).isEqualTo(DEFAULT_PRJ_APPUIS);
        assertThat(testFicheSuiviOuvrage.getNomBenef()).isEqualTo(DEFAULT_NOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getPrenomBenef()).isEqualTo(DEFAULT_PRENOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getProfessionBenef()).isEqualTo(DEFAULT_PROFESSION_BENEF);
        assertThat(testFicheSuiviOuvrage.getNbUsagers()).isEqualTo(DEFAULT_NB_USAGERS);
        assertThat(testFicheSuiviOuvrage.getContacts()).isEqualTo(DEFAULT_CONTACTS);
        assertThat(testFicheSuiviOuvrage.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testFicheSuiviOuvrage.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testFicheSuiviOuvrage.getDateRemiseDevis()).isEqualTo(DEFAULT_DATE_REMISE_DEVIS);
        assertThat(testFicheSuiviOuvrage.getDateDebutTravaux()).isEqualTo(DEFAULT_DATE_DEBUT_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getDateFinTravaux()).isEqualTo(DEFAULT_DATE_FIN_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getRue()).isEqualTo(DEFAULT_RUE);
        assertThat(testFicheSuiviOuvrage.getPorte()).isEqualTo(DEFAULT_PORTE);
        assertThat(testFicheSuiviOuvrage.getCoutMenage()).isEqualTo(DEFAULT_COUT_MENAGE);
        assertThat(testFicheSuiviOuvrage.getSubvOnea()).isEqualTo(DEFAULT_SUBV_ONEA);
        assertThat(testFicheSuiviOuvrage.getSubvProjet()).isEqualTo(DEFAULT_SUBV_PROJET);
        assertThat(testFicheSuiviOuvrage.getAutreSubv()).isEqualTo(DEFAULT_AUTRE_SUBV);
        assertThat(testFicheSuiviOuvrage.getToles()).isEqualTo(DEFAULT_TOLES);
        assertThat(testFicheSuiviOuvrage.getAnimateur()).isEqualTo(DEFAULT_ANIMATEUR);
        assertThat(testFicheSuiviOuvrage.getSuperviseur()).isEqualTo(DEFAULT_SUPERVISEUR);
        assertThat(testFicheSuiviOuvrage.getControleur()).isEqualTo(DEFAULT_CONTROLEUR);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(1)).save(testFicheSuiviOuvrage);
    }

    @Test
    @Transactional
    void createFicheSuiviOuvrageWithExistingId() throws Exception {
        // Create the FicheSuiviOuvrage with an existing ID
        ficheSuiviOuvrage.setId(1L);
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        int databaseSizeBeforeCreate = ficheSuiviOuvrageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeCreate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    @Transactional
    void checkPrjAppuisIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setPrjAppuis(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomBenefIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setNomBenef(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenomBenefIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setPrenomBenef(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProfessionBenefIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setProfessionBenef(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNbUsagersIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setNbUsagers(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContactsIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setContacts(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setLongitude(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setLatitude(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateRemiseDevisIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setDateRemiseDevis(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateDebutTravauxIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setDateDebutTravaux(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateFinTravauxIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setDateFinTravaux(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPorteIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setPorte(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCoutMenageIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setCoutMenage(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubvOneaIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setSubvOnea(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubvProjetIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setSubvProjet(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAutreSubvIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setAutreSubv(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTolesIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setToles(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAnimateurIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setAnimateur(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSuperviseurIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setSuperviseur(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkControleurIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().size();
        // set the field null
        ficheSuiviOuvrage.setControleur(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvrages() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList
        restFicheSuiviOuvrageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ficheSuiviOuvrage.getId().intValue())))
            .andExpect(jsonPath("$.[*].prjAppuis").value(hasItem(DEFAULT_PRJ_APPUIS)))
            .andExpect(jsonPath("$.[*].nomBenef").value(hasItem(DEFAULT_NOM_BENEF)))
            .andExpect(jsonPath("$.[*].prenomBenef").value(hasItem(DEFAULT_PRENOM_BENEF)))
            .andExpect(jsonPath("$.[*].professionBenef").value(hasItem(DEFAULT_PROFESSION_BENEF)))
            .andExpect(jsonPath("$.[*].nbUsagers").value(hasItem(DEFAULT_NB_USAGERS.intValue())))
            .andExpect(jsonPath("$.[*].contacts").value(hasItem(DEFAULT_CONTACTS)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].dateRemiseDevis").value(hasItem(DEFAULT_DATE_REMISE_DEVIS.toString())))
            .andExpect(jsonPath("$.[*].dateDebutTravaux").value(hasItem(DEFAULT_DATE_DEBUT_TRAVAUX.toString())))
            .andExpect(jsonPath("$.[*].dateFinTravaux").value(hasItem(DEFAULT_DATE_FIN_TRAVAUX.toString())))
            .andExpect(jsonPath("$.[*].rue").value(hasItem(DEFAULT_RUE)))
            .andExpect(jsonPath("$.[*].porte").value(hasItem(DEFAULT_PORTE)))
            .andExpect(jsonPath("$.[*].coutMenage").value(hasItem(DEFAULT_COUT_MENAGE)))
            .andExpect(jsonPath("$.[*].subvOnea").value(hasItem(DEFAULT_SUBV_ONEA)))
            .andExpect(jsonPath("$.[*].subvProjet").value(hasItem(DEFAULT_SUBV_PROJET)))
            .andExpect(jsonPath("$.[*].autreSubv").value(hasItem(DEFAULT_AUTRE_SUBV)))
            .andExpect(jsonPath("$.[*].toles").value(hasItem(DEFAULT_TOLES)))
            .andExpect(jsonPath("$.[*].animateur").value(hasItem(DEFAULT_ANIMATEUR)))
            .andExpect(jsonPath("$.[*].superviseur").value(hasItem(DEFAULT_SUPERVISEUR)))
            .andExpect(jsonPath("$.[*].controleur").value(hasItem(DEFAULT_CONTROLEUR)));
    }

    @Test
    @Transactional
    void getFicheSuiviOuvrage() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get the ficheSuiviOuvrage
        restFicheSuiviOuvrageMockMvc
            .perform(get(ENTITY_API_URL_ID, ficheSuiviOuvrage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ficheSuiviOuvrage.getId().intValue()))
            .andExpect(jsonPath("$.prjAppuis").value(DEFAULT_PRJ_APPUIS))
            .andExpect(jsonPath("$.nomBenef").value(DEFAULT_NOM_BENEF))
            .andExpect(jsonPath("$.prenomBenef").value(DEFAULT_PRENOM_BENEF))
            .andExpect(jsonPath("$.professionBenef").value(DEFAULT_PROFESSION_BENEF))
            .andExpect(jsonPath("$.nbUsagers").value(DEFAULT_NB_USAGERS.intValue()))
            .andExpect(jsonPath("$.contacts").value(DEFAULT_CONTACTS))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.dateRemiseDevis").value(DEFAULT_DATE_REMISE_DEVIS.toString()))
            .andExpect(jsonPath("$.dateDebutTravaux").value(DEFAULT_DATE_DEBUT_TRAVAUX.toString()))
            .andExpect(jsonPath("$.dateFinTravaux").value(DEFAULT_DATE_FIN_TRAVAUX.toString()))
            .andExpect(jsonPath("$.rue").value(DEFAULT_RUE))
            .andExpect(jsonPath("$.porte").value(DEFAULT_PORTE))
            .andExpect(jsonPath("$.coutMenage").value(DEFAULT_COUT_MENAGE))
            .andExpect(jsonPath("$.subvOnea").value(DEFAULT_SUBV_ONEA))
            .andExpect(jsonPath("$.subvProjet").value(DEFAULT_SUBV_PROJET))
            .andExpect(jsonPath("$.autreSubv").value(DEFAULT_AUTRE_SUBV))
            .andExpect(jsonPath("$.toles").value(DEFAULT_TOLES))
            .andExpect(jsonPath("$.animateur").value(DEFAULT_ANIMATEUR))
            .andExpect(jsonPath("$.superviseur").value(DEFAULT_SUPERVISEUR))
            .andExpect(jsonPath("$.controleur").value(DEFAULT_CONTROLEUR));
    }

    @Test
    @Transactional
    void getFicheSuiviOuvragesByIdFiltering() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        Long id = ficheSuiviOuvrage.getId();

        defaultFicheSuiviOuvrageShouldBeFound("id.equals=" + id);
        defaultFicheSuiviOuvrageShouldNotBeFound("id.notEquals=" + id);

        defaultFicheSuiviOuvrageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFicheSuiviOuvrageShouldNotBeFound("id.greaterThan=" + id);

        defaultFicheSuiviOuvrageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFicheSuiviOuvrageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrjAppuisIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prjAppuis equals to DEFAULT_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldBeFound("prjAppuis.equals=" + DEFAULT_PRJ_APPUIS);

        // Get all the ficheSuiviOuvrageList where prjAppuis equals to UPDATED_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldNotBeFound("prjAppuis.equals=" + UPDATED_PRJ_APPUIS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrjAppuisIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prjAppuis not equals to DEFAULT_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldNotBeFound("prjAppuis.notEquals=" + DEFAULT_PRJ_APPUIS);

        // Get all the ficheSuiviOuvrageList where prjAppuis not equals to UPDATED_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldBeFound("prjAppuis.notEquals=" + UPDATED_PRJ_APPUIS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrjAppuisIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prjAppuis in DEFAULT_PRJ_APPUIS or UPDATED_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldBeFound("prjAppuis.in=" + DEFAULT_PRJ_APPUIS + "," + UPDATED_PRJ_APPUIS);

        // Get all the ficheSuiviOuvrageList where prjAppuis equals to UPDATED_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldNotBeFound("prjAppuis.in=" + UPDATED_PRJ_APPUIS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrjAppuisIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prjAppuis is not null
        defaultFicheSuiviOuvrageShouldBeFound("prjAppuis.specified=true");

        // Get all the ficheSuiviOuvrageList where prjAppuis is null
        defaultFicheSuiviOuvrageShouldNotBeFound("prjAppuis.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrjAppuisContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prjAppuis contains DEFAULT_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldBeFound("prjAppuis.contains=" + DEFAULT_PRJ_APPUIS);

        // Get all the ficheSuiviOuvrageList where prjAppuis contains UPDATED_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldNotBeFound("prjAppuis.contains=" + UPDATED_PRJ_APPUIS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrjAppuisNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prjAppuis does not contain DEFAULT_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldNotBeFound("prjAppuis.doesNotContain=" + DEFAULT_PRJ_APPUIS);

        // Get all the ficheSuiviOuvrageList where prjAppuis does not contain UPDATED_PRJ_APPUIS
        defaultFicheSuiviOuvrageShouldBeFound("prjAppuis.doesNotContain=" + UPDATED_PRJ_APPUIS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNomBenefIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nomBenef equals to DEFAULT_NOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("nomBenef.equals=" + DEFAULT_NOM_BENEF);

        // Get all the ficheSuiviOuvrageList where nomBenef equals to UPDATED_NOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("nomBenef.equals=" + UPDATED_NOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNomBenefIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nomBenef not equals to DEFAULT_NOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("nomBenef.notEquals=" + DEFAULT_NOM_BENEF);

        // Get all the ficheSuiviOuvrageList where nomBenef not equals to UPDATED_NOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("nomBenef.notEquals=" + UPDATED_NOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNomBenefIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nomBenef in DEFAULT_NOM_BENEF or UPDATED_NOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("nomBenef.in=" + DEFAULT_NOM_BENEF + "," + UPDATED_NOM_BENEF);

        // Get all the ficheSuiviOuvrageList where nomBenef equals to UPDATED_NOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("nomBenef.in=" + UPDATED_NOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNomBenefIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nomBenef is not null
        defaultFicheSuiviOuvrageShouldBeFound("nomBenef.specified=true");

        // Get all the ficheSuiviOuvrageList where nomBenef is null
        defaultFicheSuiviOuvrageShouldNotBeFound("nomBenef.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNomBenefContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nomBenef contains DEFAULT_NOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("nomBenef.contains=" + DEFAULT_NOM_BENEF);

        // Get all the ficheSuiviOuvrageList where nomBenef contains UPDATED_NOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("nomBenef.contains=" + UPDATED_NOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNomBenefNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nomBenef does not contain DEFAULT_NOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("nomBenef.doesNotContain=" + DEFAULT_NOM_BENEF);

        // Get all the ficheSuiviOuvrageList where nomBenef does not contain UPDATED_NOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("nomBenef.doesNotContain=" + UPDATED_NOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrenomBenefIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prenomBenef equals to DEFAULT_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("prenomBenef.equals=" + DEFAULT_PRENOM_BENEF);

        // Get all the ficheSuiviOuvrageList where prenomBenef equals to UPDATED_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("prenomBenef.equals=" + UPDATED_PRENOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrenomBenefIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prenomBenef not equals to DEFAULT_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("prenomBenef.notEquals=" + DEFAULT_PRENOM_BENEF);

        // Get all the ficheSuiviOuvrageList where prenomBenef not equals to UPDATED_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("prenomBenef.notEquals=" + UPDATED_PRENOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrenomBenefIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prenomBenef in DEFAULT_PRENOM_BENEF or UPDATED_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("prenomBenef.in=" + DEFAULT_PRENOM_BENEF + "," + UPDATED_PRENOM_BENEF);

        // Get all the ficheSuiviOuvrageList where prenomBenef equals to UPDATED_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("prenomBenef.in=" + UPDATED_PRENOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrenomBenefIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prenomBenef is not null
        defaultFicheSuiviOuvrageShouldBeFound("prenomBenef.specified=true");

        // Get all the ficheSuiviOuvrageList where prenomBenef is null
        defaultFicheSuiviOuvrageShouldNotBeFound("prenomBenef.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrenomBenefContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prenomBenef contains DEFAULT_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("prenomBenef.contains=" + DEFAULT_PRENOM_BENEF);

        // Get all the ficheSuiviOuvrageList where prenomBenef contains UPDATED_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("prenomBenef.contains=" + UPDATED_PRENOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrenomBenefNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where prenomBenef does not contain DEFAULT_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("prenomBenef.doesNotContain=" + DEFAULT_PRENOM_BENEF);

        // Get all the ficheSuiviOuvrageList where prenomBenef does not contain UPDATED_PRENOM_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("prenomBenef.doesNotContain=" + UPDATED_PRENOM_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByProfessionBenefIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where professionBenef equals to DEFAULT_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("professionBenef.equals=" + DEFAULT_PROFESSION_BENEF);

        // Get all the ficheSuiviOuvrageList where professionBenef equals to UPDATED_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("professionBenef.equals=" + UPDATED_PROFESSION_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByProfessionBenefIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where professionBenef not equals to DEFAULT_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("professionBenef.notEquals=" + DEFAULT_PROFESSION_BENEF);

        // Get all the ficheSuiviOuvrageList where professionBenef not equals to UPDATED_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("professionBenef.notEquals=" + UPDATED_PROFESSION_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByProfessionBenefIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where professionBenef in DEFAULT_PROFESSION_BENEF or UPDATED_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("professionBenef.in=" + DEFAULT_PROFESSION_BENEF + "," + UPDATED_PROFESSION_BENEF);

        // Get all the ficheSuiviOuvrageList where professionBenef equals to UPDATED_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("professionBenef.in=" + UPDATED_PROFESSION_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByProfessionBenefIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where professionBenef is not null
        defaultFicheSuiviOuvrageShouldBeFound("professionBenef.specified=true");

        // Get all the ficheSuiviOuvrageList where professionBenef is null
        defaultFicheSuiviOuvrageShouldNotBeFound("professionBenef.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByProfessionBenefContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where professionBenef contains DEFAULT_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("professionBenef.contains=" + DEFAULT_PROFESSION_BENEF);

        // Get all the ficheSuiviOuvrageList where professionBenef contains UPDATED_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("professionBenef.contains=" + UPDATED_PROFESSION_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByProfessionBenefNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where professionBenef does not contain DEFAULT_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldNotBeFound("professionBenef.doesNotContain=" + DEFAULT_PROFESSION_BENEF);

        // Get all the ficheSuiviOuvrageList where professionBenef does not contain UPDATED_PROFESSION_BENEF
        defaultFicheSuiviOuvrageShouldBeFound("professionBenef.doesNotContain=" + UPDATED_PROFESSION_BENEF);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNbUsagersIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nbUsagers equals to DEFAULT_NB_USAGERS
        defaultFicheSuiviOuvrageShouldBeFound("nbUsagers.equals=" + DEFAULT_NB_USAGERS);

        // Get all the ficheSuiviOuvrageList where nbUsagers equals to UPDATED_NB_USAGERS
        defaultFicheSuiviOuvrageShouldNotBeFound("nbUsagers.equals=" + UPDATED_NB_USAGERS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNbUsagersIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nbUsagers not equals to DEFAULT_NB_USAGERS
        defaultFicheSuiviOuvrageShouldNotBeFound("nbUsagers.notEquals=" + DEFAULT_NB_USAGERS);

        // Get all the ficheSuiviOuvrageList where nbUsagers not equals to UPDATED_NB_USAGERS
        defaultFicheSuiviOuvrageShouldBeFound("nbUsagers.notEquals=" + UPDATED_NB_USAGERS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNbUsagersIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nbUsagers in DEFAULT_NB_USAGERS or UPDATED_NB_USAGERS
        defaultFicheSuiviOuvrageShouldBeFound("nbUsagers.in=" + DEFAULT_NB_USAGERS + "," + UPDATED_NB_USAGERS);

        // Get all the ficheSuiviOuvrageList where nbUsagers equals to UPDATED_NB_USAGERS
        defaultFicheSuiviOuvrageShouldNotBeFound("nbUsagers.in=" + UPDATED_NB_USAGERS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNbUsagersIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nbUsagers is not null
        defaultFicheSuiviOuvrageShouldBeFound("nbUsagers.specified=true");

        // Get all the ficheSuiviOuvrageList where nbUsagers is null
        defaultFicheSuiviOuvrageShouldNotBeFound("nbUsagers.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNbUsagersIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nbUsagers is greater than or equal to DEFAULT_NB_USAGERS
        defaultFicheSuiviOuvrageShouldBeFound("nbUsagers.greaterThanOrEqual=" + DEFAULT_NB_USAGERS);

        // Get all the ficheSuiviOuvrageList where nbUsagers is greater than or equal to UPDATED_NB_USAGERS
        defaultFicheSuiviOuvrageShouldNotBeFound("nbUsagers.greaterThanOrEqual=" + UPDATED_NB_USAGERS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNbUsagersIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nbUsagers is less than or equal to DEFAULT_NB_USAGERS
        defaultFicheSuiviOuvrageShouldBeFound("nbUsagers.lessThanOrEqual=" + DEFAULT_NB_USAGERS);

        // Get all the ficheSuiviOuvrageList where nbUsagers is less than or equal to SMALLER_NB_USAGERS
        defaultFicheSuiviOuvrageShouldNotBeFound("nbUsagers.lessThanOrEqual=" + SMALLER_NB_USAGERS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNbUsagersIsLessThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nbUsagers is less than DEFAULT_NB_USAGERS
        defaultFicheSuiviOuvrageShouldNotBeFound("nbUsagers.lessThan=" + DEFAULT_NB_USAGERS);

        // Get all the ficheSuiviOuvrageList where nbUsagers is less than UPDATED_NB_USAGERS
        defaultFicheSuiviOuvrageShouldBeFound("nbUsagers.lessThan=" + UPDATED_NB_USAGERS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNbUsagersIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where nbUsagers is greater than DEFAULT_NB_USAGERS
        defaultFicheSuiviOuvrageShouldNotBeFound("nbUsagers.greaterThan=" + DEFAULT_NB_USAGERS);

        // Get all the ficheSuiviOuvrageList where nbUsagers is greater than SMALLER_NB_USAGERS
        defaultFicheSuiviOuvrageShouldBeFound("nbUsagers.greaterThan=" + SMALLER_NB_USAGERS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByContactsIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where contacts equals to DEFAULT_CONTACTS
        defaultFicheSuiviOuvrageShouldBeFound("contacts.equals=" + DEFAULT_CONTACTS);

        // Get all the ficheSuiviOuvrageList where contacts equals to UPDATED_CONTACTS
        defaultFicheSuiviOuvrageShouldNotBeFound("contacts.equals=" + UPDATED_CONTACTS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByContactsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where contacts not equals to DEFAULT_CONTACTS
        defaultFicheSuiviOuvrageShouldNotBeFound("contacts.notEquals=" + DEFAULT_CONTACTS);

        // Get all the ficheSuiviOuvrageList where contacts not equals to UPDATED_CONTACTS
        defaultFicheSuiviOuvrageShouldBeFound("contacts.notEquals=" + UPDATED_CONTACTS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByContactsIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where contacts in DEFAULT_CONTACTS or UPDATED_CONTACTS
        defaultFicheSuiviOuvrageShouldBeFound("contacts.in=" + DEFAULT_CONTACTS + "," + UPDATED_CONTACTS);

        // Get all the ficheSuiviOuvrageList where contacts equals to UPDATED_CONTACTS
        defaultFicheSuiviOuvrageShouldNotBeFound("contacts.in=" + UPDATED_CONTACTS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByContactsIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where contacts is not null
        defaultFicheSuiviOuvrageShouldBeFound("contacts.specified=true");

        // Get all the ficheSuiviOuvrageList where contacts is null
        defaultFicheSuiviOuvrageShouldNotBeFound("contacts.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByContactsContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where contacts contains DEFAULT_CONTACTS
        defaultFicheSuiviOuvrageShouldBeFound("contacts.contains=" + DEFAULT_CONTACTS);

        // Get all the ficheSuiviOuvrageList where contacts contains UPDATED_CONTACTS
        defaultFicheSuiviOuvrageShouldNotBeFound("contacts.contains=" + UPDATED_CONTACTS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByContactsNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where contacts does not contain DEFAULT_CONTACTS
        defaultFicheSuiviOuvrageShouldNotBeFound("contacts.doesNotContain=" + DEFAULT_CONTACTS);

        // Get all the ficheSuiviOuvrageList where contacts does not contain UPDATED_CONTACTS
        defaultFicheSuiviOuvrageShouldBeFound("contacts.doesNotContain=" + UPDATED_CONTACTS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where longitude equals to DEFAULT_LONGITUDE
        defaultFicheSuiviOuvrageShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the ficheSuiviOuvrageList where longitude equals to UPDATED_LONGITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLongitudeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where longitude not equals to DEFAULT_LONGITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("longitude.notEquals=" + DEFAULT_LONGITUDE);

        // Get all the ficheSuiviOuvrageList where longitude not equals to UPDATED_LONGITUDE
        defaultFicheSuiviOuvrageShouldBeFound("longitude.notEquals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultFicheSuiviOuvrageShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the ficheSuiviOuvrageList where longitude equals to UPDATED_LONGITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where longitude is not null
        defaultFicheSuiviOuvrageShouldBeFound("longitude.specified=true");

        // Get all the ficheSuiviOuvrageList where longitude is null
        defaultFicheSuiviOuvrageShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultFicheSuiviOuvrageShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the ficheSuiviOuvrageList where longitude is greater than or equal to UPDATED_LONGITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultFicheSuiviOuvrageShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the ficheSuiviOuvrageList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where longitude is less than DEFAULT_LONGITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the ficheSuiviOuvrageList where longitude is less than UPDATED_LONGITUDE
        defaultFicheSuiviOuvrageShouldBeFound("longitude.lessThan=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where longitude is greater than DEFAULT_LONGITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the ficheSuiviOuvrageList where longitude is greater than SMALLER_LONGITUDE
        defaultFicheSuiviOuvrageShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where latitude equals to DEFAULT_LATITUDE
        defaultFicheSuiviOuvrageShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the ficheSuiviOuvrageList where latitude equals to UPDATED_LATITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLatitudeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where latitude not equals to DEFAULT_LATITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("latitude.notEquals=" + DEFAULT_LATITUDE);

        // Get all the ficheSuiviOuvrageList where latitude not equals to UPDATED_LATITUDE
        defaultFicheSuiviOuvrageShouldBeFound("latitude.notEquals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultFicheSuiviOuvrageShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the ficheSuiviOuvrageList where latitude equals to UPDATED_LATITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where latitude is not null
        defaultFicheSuiviOuvrageShouldBeFound("latitude.specified=true");

        // Get all the ficheSuiviOuvrageList where latitude is null
        defaultFicheSuiviOuvrageShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultFicheSuiviOuvrageShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the ficheSuiviOuvrageList where latitude is greater than or equal to UPDATED_LATITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultFicheSuiviOuvrageShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the ficheSuiviOuvrageList where latitude is less than or equal to SMALLER_LATITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where latitude is less than DEFAULT_LATITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the ficheSuiviOuvrageList where latitude is less than UPDATED_LATITUDE
        defaultFicheSuiviOuvrageShouldBeFound("latitude.lessThan=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where latitude is greater than DEFAULT_LATITUDE
        defaultFicheSuiviOuvrageShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the ficheSuiviOuvrageList where latitude is greater than SMALLER_LATITUDE
        defaultFicheSuiviOuvrageShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateRemiseDevisIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateRemiseDevis equals to DEFAULT_DATE_REMISE_DEVIS
        defaultFicheSuiviOuvrageShouldBeFound("dateRemiseDevis.equals=" + DEFAULT_DATE_REMISE_DEVIS);

        // Get all the ficheSuiviOuvrageList where dateRemiseDevis equals to UPDATED_DATE_REMISE_DEVIS
        defaultFicheSuiviOuvrageShouldNotBeFound("dateRemiseDevis.equals=" + UPDATED_DATE_REMISE_DEVIS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateRemiseDevisIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateRemiseDevis not equals to DEFAULT_DATE_REMISE_DEVIS
        defaultFicheSuiviOuvrageShouldNotBeFound("dateRemiseDevis.notEquals=" + DEFAULT_DATE_REMISE_DEVIS);

        // Get all the ficheSuiviOuvrageList where dateRemiseDevis not equals to UPDATED_DATE_REMISE_DEVIS
        defaultFicheSuiviOuvrageShouldBeFound("dateRemiseDevis.notEquals=" + UPDATED_DATE_REMISE_DEVIS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateRemiseDevisIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateRemiseDevis in DEFAULT_DATE_REMISE_DEVIS or UPDATED_DATE_REMISE_DEVIS
        defaultFicheSuiviOuvrageShouldBeFound("dateRemiseDevis.in=" + DEFAULT_DATE_REMISE_DEVIS + "," + UPDATED_DATE_REMISE_DEVIS);

        // Get all the ficheSuiviOuvrageList where dateRemiseDevis equals to UPDATED_DATE_REMISE_DEVIS
        defaultFicheSuiviOuvrageShouldNotBeFound("dateRemiseDevis.in=" + UPDATED_DATE_REMISE_DEVIS);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateRemiseDevisIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateRemiseDevis is not null
        defaultFicheSuiviOuvrageShouldBeFound("dateRemiseDevis.specified=true");

        // Get all the ficheSuiviOuvrageList where dateRemiseDevis is null
        defaultFicheSuiviOuvrageShouldNotBeFound("dateRemiseDevis.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateDebutTravauxIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateDebutTravaux equals to DEFAULT_DATE_DEBUT_TRAVAUX
        defaultFicheSuiviOuvrageShouldBeFound("dateDebutTravaux.equals=" + DEFAULT_DATE_DEBUT_TRAVAUX);

        // Get all the ficheSuiviOuvrageList where dateDebutTravaux equals to UPDATED_DATE_DEBUT_TRAVAUX
        defaultFicheSuiviOuvrageShouldNotBeFound("dateDebutTravaux.equals=" + UPDATED_DATE_DEBUT_TRAVAUX);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateDebutTravauxIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateDebutTravaux not equals to DEFAULT_DATE_DEBUT_TRAVAUX
        defaultFicheSuiviOuvrageShouldNotBeFound("dateDebutTravaux.notEquals=" + DEFAULT_DATE_DEBUT_TRAVAUX);

        // Get all the ficheSuiviOuvrageList where dateDebutTravaux not equals to UPDATED_DATE_DEBUT_TRAVAUX
        defaultFicheSuiviOuvrageShouldBeFound("dateDebutTravaux.notEquals=" + UPDATED_DATE_DEBUT_TRAVAUX);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateDebutTravauxIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateDebutTravaux in DEFAULT_DATE_DEBUT_TRAVAUX or UPDATED_DATE_DEBUT_TRAVAUX
        defaultFicheSuiviOuvrageShouldBeFound("dateDebutTravaux.in=" + DEFAULT_DATE_DEBUT_TRAVAUX + "," + UPDATED_DATE_DEBUT_TRAVAUX);

        // Get all the ficheSuiviOuvrageList where dateDebutTravaux equals to UPDATED_DATE_DEBUT_TRAVAUX
        defaultFicheSuiviOuvrageShouldNotBeFound("dateDebutTravaux.in=" + UPDATED_DATE_DEBUT_TRAVAUX);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateDebutTravauxIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateDebutTravaux is not null
        defaultFicheSuiviOuvrageShouldBeFound("dateDebutTravaux.specified=true");

        // Get all the ficheSuiviOuvrageList where dateDebutTravaux is null
        defaultFicheSuiviOuvrageShouldNotBeFound("dateDebutTravaux.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateFinTravauxIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateFinTravaux equals to DEFAULT_DATE_FIN_TRAVAUX
        defaultFicheSuiviOuvrageShouldBeFound("dateFinTravaux.equals=" + DEFAULT_DATE_FIN_TRAVAUX);

        // Get all the ficheSuiviOuvrageList where dateFinTravaux equals to UPDATED_DATE_FIN_TRAVAUX
        defaultFicheSuiviOuvrageShouldNotBeFound("dateFinTravaux.equals=" + UPDATED_DATE_FIN_TRAVAUX);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateFinTravauxIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateFinTravaux not equals to DEFAULT_DATE_FIN_TRAVAUX
        defaultFicheSuiviOuvrageShouldNotBeFound("dateFinTravaux.notEquals=" + DEFAULT_DATE_FIN_TRAVAUX);

        // Get all the ficheSuiviOuvrageList where dateFinTravaux not equals to UPDATED_DATE_FIN_TRAVAUX
        defaultFicheSuiviOuvrageShouldBeFound("dateFinTravaux.notEquals=" + UPDATED_DATE_FIN_TRAVAUX);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateFinTravauxIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateFinTravaux in DEFAULT_DATE_FIN_TRAVAUX or UPDATED_DATE_FIN_TRAVAUX
        defaultFicheSuiviOuvrageShouldBeFound("dateFinTravaux.in=" + DEFAULT_DATE_FIN_TRAVAUX + "," + UPDATED_DATE_FIN_TRAVAUX);

        // Get all the ficheSuiviOuvrageList where dateFinTravaux equals to UPDATED_DATE_FIN_TRAVAUX
        defaultFicheSuiviOuvrageShouldNotBeFound("dateFinTravaux.in=" + UPDATED_DATE_FIN_TRAVAUX);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByDateFinTravauxIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where dateFinTravaux is not null
        defaultFicheSuiviOuvrageShouldBeFound("dateFinTravaux.specified=true");

        // Get all the ficheSuiviOuvrageList where dateFinTravaux is null
        defaultFicheSuiviOuvrageShouldNotBeFound("dateFinTravaux.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByRueIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where rue equals to DEFAULT_RUE
        defaultFicheSuiviOuvrageShouldBeFound("rue.equals=" + DEFAULT_RUE);

        // Get all the ficheSuiviOuvrageList where rue equals to UPDATED_RUE
        defaultFicheSuiviOuvrageShouldNotBeFound("rue.equals=" + UPDATED_RUE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByRueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where rue not equals to DEFAULT_RUE
        defaultFicheSuiviOuvrageShouldNotBeFound("rue.notEquals=" + DEFAULT_RUE);

        // Get all the ficheSuiviOuvrageList where rue not equals to UPDATED_RUE
        defaultFicheSuiviOuvrageShouldBeFound("rue.notEquals=" + UPDATED_RUE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByRueIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where rue in DEFAULT_RUE or UPDATED_RUE
        defaultFicheSuiviOuvrageShouldBeFound("rue.in=" + DEFAULT_RUE + "," + UPDATED_RUE);

        // Get all the ficheSuiviOuvrageList where rue equals to UPDATED_RUE
        defaultFicheSuiviOuvrageShouldNotBeFound("rue.in=" + UPDATED_RUE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByRueIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where rue is not null
        defaultFicheSuiviOuvrageShouldBeFound("rue.specified=true");

        // Get all the ficheSuiviOuvrageList where rue is null
        defaultFicheSuiviOuvrageShouldNotBeFound("rue.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByRueContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where rue contains DEFAULT_RUE
        defaultFicheSuiviOuvrageShouldBeFound("rue.contains=" + DEFAULT_RUE);

        // Get all the ficheSuiviOuvrageList where rue contains UPDATED_RUE
        defaultFicheSuiviOuvrageShouldNotBeFound("rue.contains=" + UPDATED_RUE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByRueNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where rue does not contain DEFAULT_RUE
        defaultFicheSuiviOuvrageShouldNotBeFound("rue.doesNotContain=" + DEFAULT_RUE);

        // Get all the ficheSuiviOuvrageList where rue does not contain UPDATED_RUE
        defaultFicheSuiviOuvrageShouldBeFound("rue.doesNotContain=" + UPDATED_RUE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPorteIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where porte equals to DEFAULT_PORTE
        defaultFicheSuiviOuvrageShouldBeFound("porte.equals=" + DEFAULT_PORTE);

        // Get all the ficheSuiviOuvrageList where porte equals to UPDATED_PORTE
        defaultFicheSuiviOuvrageShouldNotBeFound("porte.equals=" + UPDATED_PORTE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPorteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where porte not equals to DEFAULT_PORTE
        defaultFicheSuiviOuvrageShouldNotBeFound("porte.notEquals=" + DEFAULT_PORTE);

        // Get all the ficheSuiviOuvrageList where porte not equals to UPDATED_PORTE
        defaultFicheSuiviOuvrageShouldBeFound("porte.notEquals=" + UPDATED_PORTE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPorteIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where porte in DEFAULT_PORTE or UPDATED_PORTE
        defaultFicheSuiviOuvrageShouldBeFound("porte.in=" + DEFAULT_PORTE + "," + UPDATED_PORTE);

        // Get all the ficheSuiviOuvrageList where porte equals to UPDATED_PORTE
        defaultFicheSuiviOuvrageShouldNotBeFound("porte.in=" + UPDATED_PORTE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPorteIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where porte is not null
        defaultFicheSuiviOuvrageShouldBeFound("porte.specified=true");

        // Get all the ficheSuiviOuvrageList where porte is null
        defaultFicheSuiviOuvrageShouldNotBeFound("porte.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPorteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where porte is greater than or equal to DEFAULT_PORTE
        defaultFicheSuiviOuvrageShouldBeFound("porte.greaterThanOrEqual=" + DEFAULT_PORTE);

        // Get all the ficheSuiviOuvrageList where porte is greater than or equal to UPDATED_PORTE
        defaultFicheSuiviOuvrageShouldNotBeFound("porte.greaterThanOrEqual=" + UPDATED_PORTE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPorteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where porte is less than or equal to DEFAULT_PORTE
        defaultFicheSuiviOuvrageShouldBeFound("porte.lessThanOrEqual=" + DEFAULT_PORTE);

        // Get all the ficheSuiviOuvrageList where porte is less than or equal to SMALLER_PORTE
        defaultFicheSuiviOuvrageShouldNotBeFound("porte.lessThanOrEqual=" + SMALLER_PORTE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPorteIsLessThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where porte is less than DEFAULT_PORTE
        defaultFicheSuiviOuvrageShouldNotBeFound("porte.lessThan=" + DEFAULT_PORTE);

        // Get all the ficheSuiviOuvrageList where porte is less than UPDATED_PORTE
        defaultFicheSuiviOuvrageShouldBeFound("porte.lessThan=" + UPDATED_PORTE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPorteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where porte is greater than DEFAULT_PORTE
        defaultFicheSuiviOuvrageShouldNotBeFound("porte.greaterThan=" + DEFAULT_PORTE);

        // Get all the ficheSuiviOuvrageList where porte is greater than SMALLER_PORTE
        defaultFicheSuiviOuvrageShouldBeFound("porte.greaterThan=" + SMALLER_PORTE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByCoutMenageIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where coutMenage equals to DEFAULT_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldBeFound("coutMenage.equals=" + DEFAULT_COUT_MENAGE);

        // Get all the ficheSuiviOuvrageList where coutMenage equals to UPDATED_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldNotBeFound("coutMenage.equals=" + UPDATED_COUT_MENAGE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByCoutMenageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where coutMenage not equals to DEFAULT_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldNotBeFound("coutMenage.notEquals=" + DEFAULT_COUT_MENAGE);

        // Get all the ficheSuiviOuvrageList where coutMenage not equals to UPDATED_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldBeFound("coutMenage.notEquals=" + UPDATED_COUT_MENAGE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByCoutMenageIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where coutMenage in DEFAULT_COUT_MENAGE or UPDATED_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldBeFound("coutMenage.in=" + DEFAULT_COUT_MENAGE + "," + UPDATED_COUT_MENAGE);

        // Get all the ficheSuiviOuvrageList where coutMenage equals to UPDATED_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldNotBeFound("coutMenage.in=" + UPDATED_COUT_MENAGE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByCoutMenageIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where coutMenage is not null
        defaultFicheSuiviOuvrageShouldBeFound("coutMenage.specified=true");

        // Get all the ficheSuiviOuvrageList where coutMenage is null
        defaultFicheSuiviOuvrageShouldNotBeFound("coutMenage.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByCoutMenageContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where coutMenage contains DEFAULT_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldBeFound("coutMenage.contains=" + DEFAULT_COUT_MENAGE);

        // Get all the ficheSuiviOuvrageList where coutMenage contains UPDATED_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldNotBeFound("coutMenage.contains=" + UPDATED_COUT_MENAGE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByCoutMenageNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where coutMenage does not contain DEFAULT_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldNotBeFound("coutMenage.doesNotContain=" + DEFAULT_COUT_MENAGE);

        // Get all the ficheSuiviOuvrageList where coutMenage does not contain UPDATED_COUT_MENAGE
        defaultFicheSuiviOuvrageShouldBeFound("coutMenage.doesNotContain=" + UPDATED_COUT_MENAGE);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvOneaIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvOnea equals to DEFAULT_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldBeFound("subvOnea.equals=" + DEFAULT_SUBV_ONEA);

        // Get all the ficheSuiviOuvrageList where subvOnea equals to UPDATED_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldNotBeFound("subvOnea.equals=" + UPDATED_SUBV_ONEA);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvOneaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvOnea not equals to DEFAULT_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldNotBeFound("subvOnea.notEquals=" + DEFAULT_SUBV_ONEA);

        // Get all the ficheSuiviOuvrageList where subvOnea not equals to UPDATED_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldBeFound("subvOnea.notEquals=" + UPDATED_SUBV_ONEA);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvOneaIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvOnea in DEFAULT_SUBV_ONEA or UPDATED_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldBeFound("subvOnea.in=" + DEFAULT_SUBV_ONEA + "," + UPDATED_SUBV_ONEA);

        // Get all the ficheSuiviOuvrageList where subvOnea equals to UPDATED_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldNotBeFound("subvOnea.in=" + UPDATED_SUBV_ONEA);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvOneaIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvOnea is not null
        defaultFicheSuiviOuvrageShouldBeFound("subvOnea.specified=true");

        // Get all the ficheSuiviOuvrageList where subvOnea is null
        defaultFicheSuiviOuvrageShouldNotBeFound("subvOnea.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvOneaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvOnea is greater than or equal to DEFAULT_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldBeFound("subvOnea.greaterThanOrEqual=" + DEFAULT_SUBV_ONEA);

        // Get all the ficheSuiviOuvrageList where subvOnea is greater than or equal to UPDATED_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldNotBeFound("subvOnea.greaterThanOrEqual=" + UPDATED_SUBV_ONEA);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvOneaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvOnea is less than or equal to DEFAULT_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldBeFound("subvOnea.lessThanOrEqual=" + DEFAULT_SUBV_ONEA);

        // Get all the ficheSuiviOuvrageList where subvOnea is less than or equal to SMALLER_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldNotBeFound("subvOnea.lessThanOrEqual=" + SMALLER_SUBV_ONEA);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvOneaIsLessThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvOnea is less than DEFAULT_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldNotBeFound("subvOnea.lessThan=" + DEFAULT_SUBV_ONEA);

        // Get all the ficheSuiviOuvrageList where subvOnea is less than UPDATED_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldBeFound("subvOnea.lessThan=" + UPDATED_SUBV_ONEA);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvOneaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvOnea is greater than DEFAULT_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldNotBeFound("subvOnea.greaterThan=" + DEFAULT_SUBV_ONEA);

        // Get all the ficheSuiviOuvrageList where subvOnea is greater than SMALLER_SUBV_ONEA
        defaultFicheSuiviOuvrageShouldBeFound("subvOnea.greaterThan=" + SMALLER_SUBV_ONEA);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvProjetIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvProjet equals to DEFAULT_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldBeFound("subvProjet.equals=" + DEFAULT_SUBV_PROJET);

        // Get all the ficheSuiviOuvrageList where subvProjet equals to UPDATED_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldNotBeFound("subvProjet.equals=" + UPDATED_SUBV_PROJET);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvProjetIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvProjet not equals to DEFAULT_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldNotBeFound("subvProjet.notEquals=" + DEFAULT_SUBV_PROJET);

        // Get all the ficheSuiviOuvrageList where subvProjet not equals to UPDATED_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldBeFound("subvProjet.notEquals=" + UPDATED_SUBV_PROJET);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvProjetIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvProjet in DEFAULT_SUBV_PROJET or UPDATED_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldBeFound("subvProjet.in=" + DEFAULT_SUBV_PROJET + "," + UPDATED_SUBV_PROJET);

        // Get all the ficheSuiviOuvrageList where subvProjet equals to UPDATED_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldNotBeFound("subvProjet.in=" + UPDATED_SUBV_PROJET);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvProjetIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvProjet is not null
        defaultFicheSuiviOuvrageShouldBeFound("subvProjet.specified=true");

        // Get all the ficheSuiviOuvrageList where subvProjet is null
        defaultFicheSuiviOuvrageShouldNotBeFound("subvProjet.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvProjetIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvProjet is greater than or equal to DEFAULT_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldBeFound("subvProjet.greaterThanOrEqual=" + DEFAULT_SUBV_PROJET);

        // Get all the ficheSuiviOuvrageList where subvProjet is greater than or equal to UPDATED_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldNotBeFound("subvProjet.greaterThanOrEqual=" + UPDATED_SUBV_PROJET);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvProjetIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvProjet is less than or equal to DEFAULT_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldBeFound("subvProjet.lessThanOrEqual=" + DEFAULT_SUBV_PROJET);

        // Get all the ficheSuiviOuvrageList where subvProjet is less than or equal to SMALLER_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldNotBeFound("subvProjet.lessThanOrEqual=" + SMALLER_SUBV_PROJET);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvProjetIsLessThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvProjet is less than DEFAULT_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldNotBeFound("subvProjet.lessThan=" + DEFAULT_SUBV_PROJET);

        // Get all the ficheSuiviOuvrageList where subvProjet is less than UPDATED_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldBeFound("subvProjet.lessThan=" + UPDATED_SUBV_PROJET);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySubvProjetIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where subvProjet is greater than DEFAULT_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldNotBeFound("subvProjet.greaterThan=" + DEFAULT_SUBV_PROJET);

        // Get all the ficheSuiviOuvrageList where subvProjet is greater than SMALLER_SUBV_PROJET
        defaultFicheSuiviOuvrageShouldBeFound("subvProjet.greaterThan=" + SMALLER_SUBV_PROJET);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAutreSubvIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where autreSubv equals to DEFAULT_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldBeFound("autreSubv.equals=" + DEFAULT_AUTRE_SUBV);

        // Get all the ficheSuiviOuvrageList where autreSubv equals to UPDATED_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldNotBeFound("autreSubv.equals=" + UPDATED_AUTRE_SUBV);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAutreSubvIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where autreSubv not equals to DEFAULT_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldNotBeFound("autreSubv.notEquals=" + DEFAULT_AUTRE_SUBV);

        // Get all the ficheSuiviOuvrageList where autreSubv not equals to UPDATED_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldBeFound("autreSubv.notEquals=" + UPDATED_AUTRE_SUBV);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAutreSubvIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where autreSubv in DEFAULT_AUTRE_SUBV or UPDATED_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldBeFound("autreSubv.in=" + DEFAULT_AUTRE_SUBV + "," + UPDATED_AUTRE_SUBV);

        // Get all the ficheSuiviOuvrageList where autreSubv equals to UPDATED_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldNotBeFound("autreSubv.in=" + UPDATED_AUTRE_SUBV);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAutreSubvIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where autreSubv is not null
        defaultFicheSuiviOuvrageShouldBeFound("autreSubv.specified=true");

        // Get all the ficheSuiviOuvrageList where autreSubv is null
        defaultFicheSuiviOuvrageShouldNotBeFound("autreSubv.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAutreSubvIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where autreSubv is greater than or equal to DEFAULT_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldBeFound("autreSubv.greaterThanOrEqual=" + DEFAULT_AUTRE_SUBV);

        // Get all the ficheSuiviOuvrageList where autreSubv is greater than or equal to UPDATED_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldNotBeFound("autreSubv.greaterThanOrEqual=" + UPDATED_AUTRE_SUBV);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAutreSubvIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where autreSubv is less than or equal to DEFAULT_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldBeFound("autreSubv.lessThanOrEqual=" + DEFAULT_AUTRE_SUBV);

        // Get all the ficheSuiviOuvrageList where autreSubv is less than or equal to SMALLER_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldNotBeFound("autreSubv.lessThanOrEqual=" + SMALLER_AUTRE_SUBV);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAutreSubvIsLessThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where autreSubv is less than DEFAULT_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldNotBeFound("autreSubv.lessThan=" + DEFAULT_AUTRE_SUBV);

        // Get all the ficheSuiviOuvrageList where autreSubv is less than UPDATED_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldBeFound("autreSubv.lessThan=" + UPDATED_AUTRE_SUBV);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAutreSubvIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where autreSubv is greater than DEFAULT_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldNotBeFound("autreSubv.greaterThan=" + DEFAULT_AUTRE_SUBV);

        // Get all the ficheSuiviOuvrageList where autreSubv is greater than SMALLER_AUTRE_SUBV
        defaultFicheSuiviOuvrageShouldBeFound("autreSubv.greaterThan=" + SMALLER_AUTRE_SUBV);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTolesIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where toles equals to DEFAULT_TOLES
        defaultFicheSuiviOuvrageShouldBeFound("toles.equals=" + DEFAULT_TOLES);

        // Get all the ficheSuiviOuvrageList where toles equals to UPDATED_TOLES
        defaultFicheSuiviOuvrageShouldNotBeFound("toles.equals=" + UPDATED_TOLES);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTolesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where toles not equals to DEFAULT_TOLES
        defaultFicheSuiviOuvrageShouldNotBeFound("toles.notEquals=" + DEFAULT_TOLES);

        // Get all the ficheSuiviOuvrageList where toles not equals to UPDATED_TOLES
        defaultFicheSuiviOuvrageShouldBeFound("toles.notEquals=" + UPDATED_TOLES);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTolesIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where toles in DEFAULT_TOLES or UPDATED_TOLES
        defaultFicheSuiviOuvrageShouldBeFound("toles.in=" + DEFAULT_TOLES + "," + UPDATED_TOLES);

        // Get all the ficheSuiviOuvrageList where toles equals to UPDATED_TOLES
        defaultFicheSuiviOuvrageShouldNotBeFound("toles.in=" + UPDATED_TOLES);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTolesIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where toles is not null
        defaultFicheSuiviOuvrageShouldBeFound("toles.specified=true");

        // Get all the ficheSuiviOuvrageList where toles is null
        defaultFicheSuiviOuvrageShouldNotBeFound("toles.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTolesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where toles is greater than or equal to DEFAULT_TOLES
        defaultFicheSuiviOuvrageShouldBeFound("toles.greaterThanOrEqual=" + DEFAULT_TOLES);

        // Get all the ficheSuiviOuvrageList where toles is greater than or equal to UPDATED_TOLES
        defaultFicheSuiviOuvrageShouldNotBeFound("toles.greaterThanOrEqual=" + UPDATED_TOLES);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTolesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where toles is less than or equal to DEFAULT_TOLES
        defaultFicheSuiviOuvrageShouldBeFound("toles.lessThanOrEqual=" + DEFAULT_TOLES);

        // Get all the ficheSuiviOuvrageList where toles is less than or equal to SMALLER_TOLES
        defaultFicheSuiviOuvrageShouldNotBeFound("toles.lessThanOrEqual=" + SMALLER_TOLES);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTolesIsLessThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where toles is less than DEFAULT_TOLES
        defaultFicheSuiviOuvrageShouldNotBeFound("toles.lessThan=" + DEFAULT_TOLES);

        // Get all the ficheSuiviOuvrageList where toles is less than UPDATED_TOLES
        defaultFicheSuiviOuvrageShouldBeFound("toles.lessThan=" + UPDATED_TOLES);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTolesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where toles is greater than DEFAULT_TOLES
        defaultFicheSuiviOuvrageShouldNotBeFound("toles.greaterThan=" + DEFAULT_TOLES);

        // Get all the ficheSuiviOuvrageList where toles is greater than SMALLER_TOLES
        defaultFicheSuiviOuvrageShouldBeFound("toles.greaterThan=" + SMALLER_TOLES);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAnimateurIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where animateur equals to DEFAULT_ANIMATEUR
        defaultFicheSuiviOuvrageShouldBeFound("animateur.equals=" + DEFAULT_ANIMATEUR);

        // Get all the ficheSuiviOuvrageList where animateur equals to UPDATED_ANIMATEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("animateur.equals=" + UPDATED_ANIMATEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAnimateurIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where animateur not equals to DEFAULT_ANIMATEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("animateur.notEquals=" + DEFAULT_ANIMATEUR);

        // Get all the ficheSuiviOuvrageList where animateur not equals to UPDATED_ANIMATEUR
        defaultFicheSuiviOuvrageShouldBeFound("animateur.notEquals=" + UPDATED_ANIMATEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAnimateurIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where animateur in DEFAULT_ANIMATEUR or UPDATED_ANIMATEUR
        defaultFicheSuiviOuvrageShouldBeFound("animateur.in=" + DEFAULT_ANIMATEUR + "," + UPDATED_ANIMATEUR);

        // Get all the ficheSuiviOuvrageList where animateur equals to UPDATED_ANIMATEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("animateur.in=" + UPDATED_ANIMATEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAnimateurIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where animateur is not null
        defaultFicheSuiviOuvrageShouldBeFound("animateur.specified=true");

        // Get all the ficheSuiviOuvrageList where animateur is null
        defaultFicheSuiviOuvrageShouldNotBeFound("animateur.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAnimateurContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where animateur contains DEFAULT_ANIMATEUR
        defaultFicheSuiviOuvrageShouldBeFound("animateur.contains=" + DEFAULT_ANIMATEUR);

        // Get all the ficheSuiviOuvrageList where animateur contains UPDATED_ANIMATEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("animateur.contains=" + UPDATED_ANIMATEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByAnimateurNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where animateur does not contain DEFAULT_ANIMATEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("animateur.doesNotContain=" + DEFAULT_ANIMATEUR);

        // Get all the ficheSuiviOuvrageList where animateur does not contain UPDATED_ANIMATEUR
        defaultFicheSuiviOuvrageShouldBeFound("animateur.doesNotContain=" + UPDATED_ANIMATEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySuperviseurIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where superviseur equals to DEFAULT_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldBeFound("superviseur.equals=" + DEFAULT_SUPERVISEUR);

        // Get all the ficheSuiviOuvrageList where superviseur equals to UPDATED_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("superviseur.equals=" + UPDATED_SUPERVISEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySuperviseurIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where superviseur not equals to DEFAULT_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("superviseur.notEquals=" + DEFAULT_SUPERVISEUR);

        // Get all the ficheSuiviOuvrageList where superviseur not equals to UPDATED_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldBeFound("superviseur.notEquals=" + UPDATED_SUPERVISEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySuperviseurIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where superviseur in DEFAULT_SUPERVISEUR or UPDATED_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldBeFound("superviseur.in=" + DEFAULT_SUPERVISEUR + "," + UPDATED_SUPERVISEUR);

        // Get all the ficheSuiviOuvrageList where superviseur equals to UPDATED_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("superviseur.in=" + UPDATED_SUPERVISEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySuperviseurIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where superviseur is not null
        defaultFicheSuiviOuvrageShouldBeFound("superviseur.specified=true");

        // Get all the ficheSuiviOuvrageList where superviseur is null
        defaultFicheSuiviOuvrageShouldNotBeFound("superviseur.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySuperviseurContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where superviseur contains DEFAULT_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldBeFound("superviseur.contains=" + DEFAULT_SUPERVISEUR);

        // Get all the ficheSuiviOuvrageList where superviseur contains UPDATED_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("superviseur.contains=" + UPDATED_SUPERVISEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySuperviseurNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where superviseur does not contain DEFAULT_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("superviseur.doesNotContain=" + DEFAULT_SUPERVISEUR);

        // Get all the ficheSuiviOuvrageList where superviseur does not contain UPDATED_SUPERVISEUR
        defaultFicheSuiviOuvrageShouldBeFound("superviseur.doesNotContain=" + UPDATED_SUPERVISEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByControleurIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where controleur equals to DEFAULT_CONTROLEUR
        defaultFicheSuiviOuvrageShouldBeFound("controleur.equals=" + DEFAULT_CONTROLEUR);

        // Get all the ficheSuiviOuvrageList where controleur equals to UPDATED_CONTROLEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("controleur.equals=" + UPDATED_CONTROLEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByControleurIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where controleur not equals to DEFAULT_CONTROLEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("controleur.notEquals=" + DEFAULT_CONTROLEUR);

        // Get all the ficheSuiviOuvrageList where controleur not equals to UPDATED_CONTROLEUR
        defaultFicheSuiviOuvrageShouldBeFound("controleur.notEquals=" + UPDATED_CONTROLEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByControleurIsInShouldWork() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where controleur in DEFAULT_CONTROLEUR or UPDATED_CONTROLEUR
        defaultFicheSuiviOuvrageShouldBeFound("controleur.in=" + DEFAULT_CONTROLEUR + "," + UPDATED_CONTROLEUR);

        // Get all the ficheSuiviOuvrageList where controleur equals to UPDATED_CONTROLEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("controleur.in=" + UPDATED_CONTROLEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByControleurIsNullOrNotNull() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where controleur is not null
        defaultFicheSuiviOuvrageShouldBeFound("controleur.specified=true");

        // Get all the ficheSuiviOuvrageList where controleur is null
        defaultFicheSuiviOuvrageShouldNotBeFound("controleur.specified=false");
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByControleurContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where controleur contains DEFAULT_CONTROLEUR
        defaultFicheSuiviOuvrageShouldBeFound("controleur.contains=" + DEFAULT_CONTROLEUR);

        // Get all the ficheSuiviOuvrageList where controleur contains UPDATED_CONTROLEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("controleur.contains=" + UPDATED_CONTROLEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByControleurNotContainsSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        // Get all the ficheSuiviOuvrageList where controleur does not contain DEFAULT_CONTROLEUR
        defaultFicheSuiviOuvrageShouldNotBeFound("controleur.doesNotContain=" + DEFAULT_CONTROLEUR);

        // Get all the ficheSuiviOuvrageList where controleur does not contain UPDATED_CONTROLEUR
        defaultFicheSuiviOuvrageShouldBeFound("controleur.doesNotContain=" + UPDATED_CONTROLEUR);
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrevisionIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Prevision prevision = PrevisionResourceIT.createEntity(em);
        em.persist(prevision);
        em.flush();
        ficheSuiviOuvrage.setPrevision(prevision);
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Long previsionId = prevision.getId();

        // Get all the ficheSuiviOuvrageList where prevision equals to previsionId
        defaultFicheSuiviOuvrageShouldBeFound("previsionId.equals=" + previsionId);

        // Get all the ficheSuiviOuvrageList where prevision equals to (previsionId + 1)
        defaultFicheSuiviOuvrageShouldNotBeFound("previsionId.equals=" + (previsionId + 1));
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByNatureouvrageIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        NatureOuvrage natureouvrage = NatureOuvrageResourceIT.createEntity(em);
        em.persist(natureouvrage);
        em.flush();
        ficheSuiviOuvrage.setNatureouvrage(natureouvrage);
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Long natureouvrageId = natureouvrage.getId();

        // Get all the ficheSuiviOuvrageList where natureouvrage equals to natureouvrageId
        defaultFicheSuiviOuvrageShouldBeFound("natureouvrageId.equals=" + natureouvrageId);

        // Get all the ficheSuiviOuvrageList where natureouvrage equals to (natureouvrageId + 1)
        defaultFicheSuiviOuvrageShouldNotBeFound("natureouvrageId.equals=" + (natureouvrageId + 1));
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByTypehabitationIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        TypeHabitation typehabitation = TypeHabitationResourceIT.createEntity(em);
        em.persist(typehabitation);
        em.flush();
        ficheSuiviOuvrage.setTypehabitation(typehabitation);
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Long typehabitationId = typehabitation.getId();

        // Get all the ficheSuiviOuvrageList where typehabitation equals to typehabitationId
        defaultFicheSuiviOuvrageShouldBeFound("typehabitationId.equals=" + typehabitationId);

        // Get all the ficheSuiviOuvrageList where typehabitation equals to (typehabitationId + 1)
        defaultFicheSuiviOuvrageShouldNotBeFound("typehabitationId.equals=" + (typehabitationId + 1));
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesBySourceapprovepIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        SourceApprovEp sourceapprovep = SourceApprovEpResourceIT.createEntity(em);
        em.persist(sourceapprovep);
        em.flush();
        ficheSuiviOuvrage.setSourceapprovep(sourceapprovep);
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Long sourceapprovepId = sourceapprovep.getId();

        // Get all the ficheSuiviOuvrageList where sourceapprovep equals to sourceapprovepId
        defaultFicheSuiviOuvrageShouldBeFound("sourceapprovepId.equals=" + sourceapprovepId);

        // Get all the ficheSuiviOuvrageList where sourceapprovep equals to (sourceapprovepId + 1)
        defaultFicheSuiviOuvrageShouldNotBeFound("sourceapprovepId.equals=" + (sourceapprovepId + 1));
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByModeevacuationeauuseeIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        ModeEvacuationEauUsee modeevacuationeauusee = ModeEvacuationEauUseeResourceIT.createEntity(em);
        em.persist(modeevacuationeauusee);
        em.flush();
        ficheSuiviOuvrage.setModeevacuationeauusee(modeevacuationeauusee);
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Long modeevacuationeauuseeId = modeevacuationeauusee.getId();

        // Get all the ficheSuiviOuvrageList where modeevacuationeauusee equals to modeevacuationeauuseeId
        defaultFicheSuiviOuvrageShouldBeFound("modeevacuationeauuseeId.equals=" + modeevacuationeauuseeId);

        // Get all the ficheSuiviOuvrageList where modeevacuationeauusee equals to (modeevacuationeauuseeId + 1)
        defaultFicheSuiviOuvrageShouldNotBeFound("modeevacuationeauuseeId.equals=" + (modeevacuationeauuseeId + 1));
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByModeevacexcretaIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        ModeEvacExcreta modeevacexcreta = ModeEvacExcretaResourceIT.createEntity(em);
        em.persist(modeevacexcreta);
        em.flush();
        ficheSuiviOuvrage.setModeevacexcreta(modeevacexcreta);
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Long modeevacexcretaId = modeevacexcreta.getId();

        // Get all the ficheSuiviOuvrageList where modeevacexcreta equals to modeevacexcretaId
        defaultFicheSuiviOuvrageShouldBeFound("modeevacexcretaId.equals=" + modeevacexcretaId);

        // Get all the ficheSuiviOuvrageList where modeevacexcreta equals to (modeevacexcretaId + 1)
        defaultFicheSuiviOuvrageShouldNotBeFound("modeevacexcretaId.equals=" + (modeevacexcretaId + 1));
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByMaconIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Macon macon = MaconResourceIT.createEntity(em);
        em.persist(macon);
        em.flush();
        ficheSuiviOuvrage.setMacon(macon);
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Long maconId = macon.getId();

        // Get all the ficheSuiviOuvrageList where macon equals to maconId
        defaultFicheSuiviOuvrageShouldBeFound("maconId.equals=" + maconId);

        // Get all the ficheSuiviOuvrageList where macon equals to (maconId + 1)
        defaultFicheSuiviOuvrageShouldNotBeFound("maconId.equals=" + (maconId + 1));
    }

    @Test
    @Transactional
    void getAllFicheSuiviOuvragesByPrefabricantIsEqualToSomething() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Prefabricant prefabricant = PrefabricantResourceIT.createEntity(em);
        em.persist(prefabricant);
        em.flush();
        ficheSuiviOuvrage.setPrefabricant(prefabricant);
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        Long prefabricantId = prefabricant.getId();

        // Get all the ficheSuiviOuvrageList where prefabricant equals to prefabricantId
        defaultFicheSuiviOuvrageShouldBeFound("prefabricantId.equals=" + prefabricantId);

        // Get all the ficheSuiviOuvrageList where prefabricant equals to (prefabricantId + 1)
        defaultFicheSuiviOuvrageShouldNotBeFound("prefabricantId.equals=" + (prefabricantId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFicheSuiviOuvrageShouldBeFound(String filter) throws Exception {
        restFicheSuiviOuvrageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ficheSuiviOuvrage.getId().intValue())))
            .andExpect(jsonPath("$.[*].prjAppuis").value(hasItem(DEFAULT_PRJ_APPUIS)))
            .andExpect(jsonPath("$.[*].nomBenef").value(hasItem(DEFAULT_NOM_BENEF)))
            .andExpect(jsonPath("$.[*].prenomBenef").value(hasItem(DEFAULT_PRENOM_BENEF)))
            .andExpect(jsonPath("$.[*].professionBenef").value(hasItem(DEFAULT_PROFESSION_BENEF)))
            .andExpect(jsonPath("$.[*].nbUsagers").value(hasItem(DEFAULT_NB_USAGERS.intValue())))
            .andExpect(jsonPath("$.[*].contacts").value(hasItem(DEFAULT_CONTACTS)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].dateRemiseDevis").value(hasItem(DEFAULT_DATE_REMISE_DEVIS.toString())))
            .andExpect(jsonPath("$.[*].dateDebutTravaux").value(hasItem(DEFAULT_DATE_DEBUT_TRAVAUX.toString())))
            .andExpect(jsonPath("$.[*].dateFinTravaux").value(hasItem(DEFAULT_DATE_FIN_TRAVAUX.toString())))
            .andExpect(jsonPath("$.[*].rue").value(hasItem(DEFAULT_RUE)))
            .andExpect(jsonPath("$.[*].porte").value(hasItem(DEFAULT_PORTE)))
            .andExpect(jsonPath("$.[*].coutMenage").value(hasItem(DEFAULT_COUT_MENAGE)))
            .andExpect(jsonPath("$.[*].subvOnea").value(hasItem(DEFAULT_SUBV_ONEA)))
            .andExpect(jsonPath("$.[*].subvProjet").value(hasItem(DEFAULT_SUBV_PROJET)))
            .andExpect(jsonPath("$.[*].autreSubv").value(hasItem(DEFAULT_AUTRE_SUBV)))
            .andExpect(jsonPath("$.[*].toles").value(hasItem(DEFAULT_TOLES)))
            .andExpect(jsonPath("$.[*].animateur").value(hasItem(DEFAULT_ANIMATEUR)))
            .andExpect(jsonPath("$.[*].superviseur").value(hasItem(DEFAULT_SUPERVISEUR)))
            .andExpect(jsonPath("$.[*].controleur").value(hasItem(DEFAULT_CONTROLEUR)));

        // Check, that the count call also returns 1
        restFicheSuiviOuvrageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFicheSuiviOuvrageShouldNotBeFound(String filter) throws Exception {
        restFicheSuiviOuvrageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFicheSuiviOuvrageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFicheSuiviOuvrage() throws Exception {
        // Get the ficheSuiviOuvrage
        restFicheSuiviOuvrageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFicheSuiviOuvrage() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();

        // Update the ficheSuiviOuvrage
        FicheSuiviOuvrage updatedFicheSuiviOuvrage = ficheSuiviOuvrageRepository.findById(ficheSuiviOuvrage.getId()).get();
        // Disconnect from session so that the updates on updatedFicheSuiviOuvrage are not directly saved in db
        em.detach(updatedFicheSuiviOuvrage);
        updatedFicheSuiviOuvrage
            .prjAppuis(UPDATED_PRJ_APPUIS)
            .nomBenef(UPDATED_NOM_BENEF)
            .prenomBenef(UPDATED_PRENOM_BENEF)
            .professionBenef(UPDATED_PROFESSION_BENEF)
            .nbUsagers(UPDATED_NB_USAGERS)
            .contacts(UPDATED_CONTACTS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .dateRemiseDevis(UPDATED_DATE_REMISE_DEVIS)
            .dateDebutTravaux(UPDATED_DATE_DEBUT_TRAVAUX)
            .dateFinTravaux(UPDATED_DATE_FIN_TRAVAUX)
            .rue(UPDATED_RUE)
            .porte(UPDATED_PORTE)
            .coutMenage(UPDATED_COUT_MENAGE)
            .subvOnea(UPDATED_SUBV_ONEA)
            .subvProjet(UPDATED_SUBV_PROJET)
            .autreSubv(UPDATED_AUTRE_SUBV)
            .toles(UPDATED_TOLES)
            .animateur(UPDATED_ANIMATEUR)
            .superviseur(UPDATED_SUPERVISEUR)
            .controleur(UPDATED_CONTROLEUR);
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(updatedFicheSuiviOuvrage);

        restFicheSuiviOuvrageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ficheSuiviOuvrageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isOk());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);
        FicheSuiviOuvrage testFicheSuiviOuvrage = ficheSuiviOuvrageList.get(ficheSuiviOuvrageList.size() - 1);
        assertThat(testFicheSuiviOuvrage.getPrjAppuis()).isEqualTo(UPDATED_PRJ_APPUIS);
        assertThat(testFicheSuiviOuvrage.getNomBenef()).isEqualTo(UPDATED_NOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getPrenomBenef()).isEqualTo(UPDATED_PRENOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getProfessionBenef()).isEqualTo(UPDATED_PROFESSION_BENEF);
        assertThat(testFicheSuiviOuvrage.getNbUsagers()).isEqualTo(UPDATED_NB_USAGERS);
        assertThat(testFicheSuiviOuvrage.getContacts()).isEqualTo(UPDATED_CONTACTS);
        assertThat(testFicheSuiviOuvrage.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testFicheSuiviOuvrage.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testFicheSuiviOuvrage.getDateRemiseDevis()).isEqualTo(UPDATED_DATE_REMISE_DEVIS);
        assertThat(testFicheSuiviOuvrage.getDateDebutTravaux()).isEqualTo(UPDATED_DATE_DEBUT_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getDateFinTravaux()).isEqualTo(UPDATED_DATE_FIN_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getRue()).isEqualTo(UPDATED_RUE);
        assertThat(testFicheSuiviOuvrage.getPorte()).isEqualTo(UPDATED_PORTE);
        assertThat(testFicheSuiviOuvrage.getCoutMenage()).isEqualTo(UPDATED_COUT_MENAGE);
        assertThat(testFicheSuiviOuvrage.getSubvOnea()).isEqualTo(UPDATED_SUBV_ONEA);
        assertThat(testFicheSuiviOuvrage.getSubvProjet()).isEqualTo(UPDATED_SUBV_PROJET);
        assertThat(testFicheSuiviOuvrage.getAutreSubv()).isEqualTo(UPDATED_AUTRE_SUBV);
        assertThat(testFicheSuiviOuvrage.getToles()).isEqualTo(UPDATED_TOLES);
        assertThat(testFicheSuiviOuvrage.getAnimateur()).isEqualTo(UPDATED_ANIMATEUR);
        assertThat(testFicheSuiviOuvrage.getSuperviseur()).isEqualTo(UPDATED_SUPERVISEUR);
        assertThat(testFicheSuiviOuvrage.getControleur()).isEqualTo(UPDATED_CONTROLEUR);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository).save(testFicheSuiviOuvrage);
    }

    @Test
    @Transactional
    void putNonExistingFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFicheSuiviOuvrageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ficheSuiviOuvrageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    @Transactional
    void putWithIdMismatchFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFicheSuiviOuvrageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFicheSuiviOuvrageMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    @Transactional
    void partialUpdateFicheSuiviOuvrageWithPatch() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();

        // Update the ficheSuiviOuvrage using partial update
        FicheSuiviOuvrage partialUpdatedFicheSuiviOuvrage = new FicheSuiviOuvrage();
        partialUpdatedFicheSuiviOuvrage.setId(ficheSuiviOuvrage.getId());

        partialUpdatedFicheSuiviOuvrage
            .nbUsagers(UPDATED_NB_USAGERS)
            .contacts(UPDATED_CONTACTS)
            .dateRemiseDevis(UPDATED_DATE_REMISE_DEVIS)
            .dateFinTravaux(UPDATED_DATE_FIN_TRAVAUX)
            .rue(UPDATED_RUE)
            .coutMenage(UPDATED_COUT_MENAGE)
            .subvProjet(UPDATED_SUBV_PROJET)
            .autreSubv(UPDATED_AUTRE_SUBV)
            .toles(UPDATED_TOLES)
            .animateur(UPDATED_ANIMATEUR)
            .superviseur(UPDATED_SUPERVISEUR)
            .controleur(UPDATED_CONTROLEUR);

        restFicheSuiviOuvrageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFicheSuiviOuvrage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFicheSuiviOuvrage))
            )
            .andExpect(status().isOk());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);
        FicheSuiviOuvrage testFicheSuiviOuvrage = ficheSuiviOuvrageList.get(ficheSuiviOuvrageList.size() - 1);
        assertThat(testFicheSuiviOuvrage.getPrjAppuis()).isEqualTo(DEFAULT_PRJ_APPUIS);
        assertThat(testFicheSuiviOuvrage.getNomBenef()).isEqualTo(DEFAULT_NOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getPrenomBenef()).isEqualTo(DEFAULT_PRENOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getProfessionBenef()).isEqualTo(DEFAULT_PROFESSION_BENEF);
        assertThat(testFicheSuiviOuvrage.getNbUsagers()).isEqualTo(UPDATED_NB_USAGERS);
        assertThat(testFicheSuiviOuvrage.getContacts()).isEqualTo(UPDATED_CONTACTS);
        assertThat(testFicheSuiviOuvrage.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testFicheSuiviOuvrage.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testFicheSuiviOuvrage.getDateRemiseDevis()).isEqualTo(UPDATED_DATE_REMISE_DEVIS);
        assertThat(testFicheSuiviOuvrage.getDateDebutTravaux()).isEqualTo(DEFAULT_DATE_DEBUT_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getDateFinTravaux()).isEqualTo(UPDATED_DATE_FIN_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getRue()).isEqualTo(UPDATED_RUE);
        assertThat(testFicheSuiviOuvrage.getPorte()).isEqualTo(DEFAULT_PORTE);
        assertThat(testFicheSuiviOuvrage.getCoutMenage()).isEqualTo(UPDATED_COUT_MENAGE);
        assertThat(testFicheSuiviOuvrage.getSubvOnea()).isEqualTo(DEFAULT_SUBV_ONEA);
        assertThat(testFicheSuiviOuvrage.getSubvProjet()).isEqualTo(UPDATED_SUBV_PROJET);
        assertThat(testFicheSuiviOuvrage.getAutreSubv()).isEqualTo(UPDATED_AUTRE_SUBV);
        assertThat(testFicheSuiviOuvrage.getToles()).isEqualTo(UPDATED_TOLES);
        assertThat(testFicheSuiviOuvrage.getAnimateur()).isEqualTo(UPDATED_ANIMATEUR);
        assertThat(testFicheSuiviOuvrage.getSuperviseur()).isEqualTo(UPDATED_SUPERVISEUR);
        assertThat(testFicheSuiviOuvrage.getControleur()).isEqualTo(UPDATED_CONTROLEUR);
    }

    @Test
    @Transactional
    void fullUpdateFicheSuiviOuvrageWithPatch() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();

        // Update the ficheSuiviOuvrage using partial update
        FicheSuiviOuvrage partialUpdatedFicheSuiviOuvrage = new FicheSuiviOuvrage();
        partialUpdatedFicheSuiviOuvrage.setId(ficheSuiviOuvrage.getId());

        partialUpdatedFicheSuiviOuvrage
            .prjAppuis(UPDATED_PRJ_APPUIS)
            .nomBenef(UPDATED_NOM_BENEF)
            .prenomBenef(UPDATED_PRENOM_BENEF)
            .professionBenef(UPDATED_PROFESSION_BENEF)
            .nbUsagers(UPDATED_NB_USAGERS)
            .contacts(UPDATED_CONTACTS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .dateRemiseDevis(UPDATED_DATE_REMISE_DEVIS)
            .dateDebutTravaux(UPDATED_DATE_DEBUT_TRAVAUX)
            .dateFinTravaux(UPDATED_DATE_FIN_TRAVAUX)
            .rue(UPDATED_RUE)
            .porte(UPDATED_PORTE)
            .coutMenage(UPDATED_COUT_MENAGE)
            .subvOnea(UPDATED_SUBV_ONEA)
            .subvProjet(UPDATED_SUBV_PROJET)
            .autreSubv(UPDATED_AUTRE_SUBV)
            .toles(UPDATED_TOLES)
            .animateur(UPDATED_ANIMATEUR)
            .superviseur(UPDATED_SUPERVISEUR)
            .controleur(UPDATED_CONTROLEUR);

        restFicheSuiviOuvrageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFicheSuiviOuvrage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFicheSuiviOuvrage))
            )
            .andExpect(status().isOk());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);
        FicheSuiviOuvrage testFicheSuiviOuvrage = ficheSuiviOuvrageList.get(ficheSuiviOuvrageList.size() - 1);
        assertThat(testFicheSuiviOuvrage.getPrjAppuis()).isEqualTo(UPDATED_PRJ_APPUIS);
        assertThat(testFicheSuiviOuvrage.getNomBenef()).isEqualTo(UPDATED_NOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getPrenomBenef()).isEqualTo(UPDATED_PRENOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getProfessionBenef()).isEqualTo(UPDATED_PROFESSION_BENEF);
        assertThat(testFicheSuiviOuvrage.getNbUsagers()).isEqualTo(UPDATED_NB_USAGERS);
        assertThat(testFicheSuiviOuvrage.getContacts()).isEqualTo(UPDATED_CONTACTS);
        assertThat(testFicheSuiviOuvrage.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testFicheSuiviOuvrage.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testFicheSuiviOuvrage.getDateRemiseDevis()).isEqualTo(UPDATED_DATE_REMISE_DEVIS);
        assertThat(testFicheSuiviOuvrage.getDateDebutTravaux()).isEqualTo(UPDATED_DATE_DEBUT_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getDateFinTravaux()).isEqualTo(UPDATED_DATE_FIN_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getRue()).isEqualTo(UPDATED_RUE);
        assertThat(testFicheSuiviOuvrage.getPorte()).isEqualTo(UPDATED_PORTE);
        assertThat(testFicheSuiviOuvrage.getCoutMenage()).isEqualTo(UPDATED_COUT_MENAGE);
        assertThat(testFicheSuiviOuvrage.getSubvOnea()).isEqualTo(UPDATED_SUBV_ONEA);
        assertThat(testFicheSuiviOuvrage.getSubvProjet()).isEqualTo(UPDATED_SUBV_PROJET);
        assertThat(testFicheSuiviOuvrage.getAutreSubv()).isEqualTo(UPDATED_AUTRE_SUBV);
        assertThat(testFicheSuiviOuvrage.getToles()).isEqualTo(UPDATED_TOLES);
        assertThat(testFicheSuiviOuvrage.getAnimateur()).isEqualTo(UPDATED_ANIMATEUR);
        assertThat(testFicheSuiviOuvrage.getSuperviseur()).isEqualTo(UPDATED_SUPERVISEUR);
        assertThat(testFicheSuiviOuvrage.getControleur()).isEqualTo(UPDATED_CONTROLEUR);
    }

    @Test
    @Transactional
    void patchNonExistingFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFicheSuiviOuvrageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ficheSuiviOuvrageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFicheSuiviOuvrageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFicheSuiviOuvrageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    @Transactional
    void deleteFicheSuiviOuvrage() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);

        int databaseSizeBeforeDelete = ficheSuiviOuvrageRepository.findAll().size();

        // Delete the ficheSuiviOuvrage
        restFicheSuiviOuvrageMockMvc
            .perform(delete(ENTITY_API_URL_ID, ficheSuiviOuvrage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(1)).deleteById(ficheSuiviOuvrage.getId());
    }

    @Test
    @Transactional
    void searchFicheSuiviOuvrage() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        ficheSuiviOuvrageRepository.saveAndFlush(ficheSuiviOuvrage);
        when(mockFicheSuiviOuvrageSearchRepository.search(queryStringQuery("id:" + ficheSuiviOuvrage.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(ficheSuiviOuvrage), PageRequest.of(0, 1), 1));

        // Search the ficheSuiviOuvrage
        restFicheSuiviOuvrageMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ficheSuiviOuvrage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ficheSuiviOuvrage.getId().intValue())))
            .andExpect(jsonPath("$.[*].prjAppuis").value(hasItem(DEFAULT_PRJ_APPUIS)))
            .andExpect(jsonPath("$.[*].nomBenef").value(hasItem(DEFAULT_NOM_BENEF)))
            .andExpect(jsonPath("$.[*].prenomBenef").value(hasItem(DEFAULT_PRENOM_BENEF)))
            .andExpect(jsonPath("$.[*].professionBenef").value(hasItem(DEFAULT_PROFESSION_BENEF)))
            .andExpect(jsonPath("$.[*].nbUsagers").value(hasItem(DEFAULT_NB_USAGERS.intValue())))
            .andExpect(jsonPath("$.[*].contacts").value(hasItem(DEFAULT_CONTACTS)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].dateRemiseDevis").value(hasItem(DEFAULT_DATE_REMISE_DEVIS.toString())))
            .andExpect(jsonPath("$.[*].dateDebutTravaux").value(hasItem(DEFAULT_DATE_DEBUT_TRAVAUX.toString())))
            .andExpect(jsonPath("$.[*].dateFinTravaux").value(hasItem(DEFAULT_DATE_FIN_TRAVAUX.toString())))
            .andExpect(jsonPath("$.[*].rue").value(hasItem(DEFAULT_RUE)))
            .andExpect(jsonPath("$.[*].porte").value(hasItem(DEFAULT_PORTE)))
            .andExpect(jsonPath("$.[*].coutMenage").value(hasItem(DEFAULT_COUT_MENAGE)))
            .andExpect(jsonPath("$.[*].subvOnea").value(hasItem(DEFAULT_SUBV_ONEA)))
            .andExpect(jsonPath("$.[*].subvProjet").value(hasItem(DEFAULT_SUBV_PROJET)))
            .andExpect(jsonPath("$.[*].autreSubv").value(hasItem(DEFAULT_AUTRE_SUBV)))
            .andExpect(jsonPath("$.[*].toles").value(hasItem(DEFAULT_TOLES)))
            .andExpect(jsonPath("$.[*].animateur").value(hasItem(DEFAULT_ANIMATEUR)))
            .andExpect(jsonPath("$.[*].superviseur").value(hasItem(DEFAULT_SUPERVISEUR)))
            .andExpect(jsonPath("$.[*].controleur").value(hasItem(DEFAULT_CONTROLEUR)));
    }
}
