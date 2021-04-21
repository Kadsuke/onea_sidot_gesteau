package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import com.sidot.gesteau.repository.FicheSuiviOuvrageRepository;
import com.sidot.gesteau.repository.search.FicheSuiviOuvrageSearchRepository;
import com.sidot.gesteau.service.criteria.FicheSuiviOuvrageCriteria;
import com.sidot.gesteau.service.dto.FicheSuiviOuvrageDTO;
import com.sidot.gesteau.service.mapper.FicheSuiviOuvrageMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FicheSuiviOuvrage} entities in the database.
 * The main input is a {@link FicheSuiviOuvrageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FicheSuiviOuvrageDTO} or a {@link Page} of {@link FicheSuiviOuvrageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FicheSuiviOuvrageQueryService extends QueryService<FicheSuiviOuvrage> {

    private final Logger log = LoggerFactory.getLogger(FicheSuiviOuvrageQueryService.class);

    private final FicheSuiviOuvrageRepository ficheSuiviOuvrageRepository;

    private final FicheSuiviOuvrageMapper ficheSuiviOuvrageMapper;

    private final FicheSuiviOuvrageSearchRepository ficheSuiviOuvrageSearchRepository;

    public FicheSuiviOuvrageQueryService(
        FicheSuiviOuvrageRepository ficheSuiviOuvrageRepository,
        FicheSuiviOuvrageMapper ficheSuiviOuvrageMapper,
        FicheSuiviOuvrageSearchRepository ficheSuiviOuvrageSearchRepository
    ) {
        this.ficheSuiviOuvrageRepository = ficheSuiviOuvrageRepository;
        this.ficheSuiviOuvrageMapper = ficheSuiviOuvrageMapper;
        this.ficheSuiviOuvrageSearchRepository = ficheSuiviOuvrageSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FicheSuiviOuvrageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FicheSuiviOuvrageDTO> findByCriteria(FicheSuiviOuvrageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FicheSuiviOuvrage> specification = createSpecification(criteria);
        return ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrageRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FicheSuiviOuvrageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FicheSuiviOuvrageDTO> findByCriteria(FicheSuiviOuvrageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FicheSuiviOuvrage> specification = createSpecification(criteria);
        return ficheSuiviOuvrageRepository.findAll(specification, page).map(ficheSuiviOuvrageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FicheSuiviOuvrageCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FicheSuiviOuvrage> specification = createSpecification(criteria);
        return ficheSuiviOuvrageRepository.count(specification);
    }

    /**
     * Function to convert {@link FicheSuiviOuvrageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FicheSuiviOuvrage> createSpecification(FicheSuiviOuvrageCriteria criteria) {
        Specification<FicheSuiviOuvrage> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FicheSuiviOuvrage_.id));
            }
            if (criteria.getPrjAppuis() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrjAppuis(), FicheSuiviOuvrage_.prjAppuis));
            }
            if (criteria.getNomBenef() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomBenef(), FicheSuiviOuvrage_.nomBenef));
            }
            if (criteria.getPrenomBenef() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrenomBenef(), FicheSuiviOuvrage_.prenomBenef));
            }
            if (criteria.getProfessionBenef() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getProfessionBenef(), FicheSuiviOuvrage_.professionBenef));
            }
            if (criteria.getNbUsagers() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNbUsagers(), FicheSuiviOuvrage_.nbUsagers));
            }
            if (criteria.getContacts() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContacts(), FicheSuiviOuvrage_.contacts));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), FicheSuiviOuvrage_.longitude));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), FicheSuiviOuvrage_.latitude));
            }
            if (criteria.getDateRemiseDevis() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getDateRemiseDevis(), FicheSuiviOuvrage_.dateRemiseDevis));
            }
            if (criteria.getDateDebutTravaux() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getDateDebutTravaux(), FicheSuiviOuvrage_.dateDebutTravaux));
            }
            if (criteria.getDateFinTravaux() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateFinTravaux(), FicheSuiviOuvrage_.dateFinTravaux));
            }
            if (criteria.getRue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRue(), FicheSuiviOuvrage_.rue));
            }
            if (criteria.getPorte() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPorte(), FicheSuiviOuvrage_.porte));
            }
            if (criteria.getCoutMenage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCoutMenage(), FicheSuiviOuvrage_.coutMenage));
            }
            if (criteria.getSubvOnea() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSubvOnea(), FicheSuiviOuvrage_.subvOnea));
            }
            if (criteria.getSubvProjet() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSubvProjet(), FicheSuiviOuvrage_.subvProjet));
            }
            if (criteria.getAutreSubv() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAutreSubv(), FicheSuiviOuvrage_.autreSubv));
            }
            if (criteria.getToles() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getToles(), FicheSuiviOuvrage_.toles));
            }
            if (criteria.getAnimateur() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAnimateur(), FicheSuiviOuvrage_.animateur));
            }
            if (criteria.getSuperviseur() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSuperviseur(), FicheSuiviOuvrage_.superviseur));
            }
            if (criteria.getControleur() != null) {
                specification = specification.and(buildStringSpecification(criteria.getControleur(), FicheSuiviOuvrage_.controleur));
            }
            if (criteria.getPrevisionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPrevisionId(),
                            root -> root.join(FicheSuiviOuvrage_.prevision, JoinType.LEFT).get(Prevision_.id)
                        )
                    );
            }
            if (criteria.getNatureouvrageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getNatureouvrageId(),
                            root -> root.join(FicheSuiviOuvrage_.natureouvrage, JoinType.LEFT).get(NatureOuvrage_.id)
                        )
                    );
            }
            if (criteria.getTypehabitationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTypehabitationId(),
                            root -> root.join(FicheSuiviOuvrage_.typehabitation, JoinType.LEFT).get(TypeHabitation_.id)
                        )
                    );
            }
            if (criteria.getSourceapprovepId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSourceapprovepId(),
                            root -> root.join(FicheSuiviOuvrage_.sourceapprovep, JoinType.LEFT).get(SourceApprovEp_.id)
                        )
                    );
            }
            if (criteria.getModeevacuationeauuseeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getModeevacuationeauuseeId(),
                            root -> root.join(FicheSuiviOuvrage_.modeevacuationeauusee, JoinType.LEFT).get(ModeEvacuationEauUsee_.id)
                        )
                    );
            }
            if (criteria.getModeevacexcretaId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getModeevacexcretaId(),
                            root -> root.join(FicheSuiviOuvrage_.modeevacexcreta, JoinType.LEFT).get(ModeEvacExcreta_.id)
                        )
                    );
            }
            if (criteria.getMaconId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getMaconId(), root -> root.join(FicheSuiviOuvrage_.macon, JoinType.LEFT).get(Macon_.id))
                    );
            }
            if (criteria.getPrefabricantId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPrefabricantId(),
                            root -> root.join(FicheSuiviOuvrage_.prefabricant, JoinType.LEFT).get(Prefabricant_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
