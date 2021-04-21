package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.Prevision;
import com.sidot.gesteau.repository.PrevisionRepository;
import com.sidot.gesteau.repository.search.PrevisionSearchRepository;
import com.sidot.gesteau.service.criteria.PrevisionCriteria;
import com.sidot.gesteau.service.dto.PrevisionDTO;
import com.sidot.gesteau.service.mapper.PrevisionMapper;
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
 * Service for executing complex queries for {@link Prevision} entities in the database.
 * The main input is a {@link PrevisionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrevisionDTO} or a {@link Page} of {@link PrevisionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrevisionQueryService extends QueryService<Prevision> {

    private final Logger log = LoggerFactory.getLogger(PrevisionQueryService.class);

    private final PrevisionRepository previsionRepository;

    private final PrevisionMapper previsionMapper;

    private final PrevisionSearchRepository previsionSearchRepository;

    public PrevisionQueryService(
        PrevisionRepository previsionRepository,
        PrevisionMapper previsionMapper,
        PrevisionSearchRepository previsionSearchRepository
    ) {
        this.previsionRepository = previsionRepository;
        this.previsionMapper = previsionMapper;
        this.previsionSearchRepository = previsionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PrevisionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrevisionDTO> findByCriteria(PrevisionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Prevision> specification = createSpecification(criteria);
        return previsionMapper.toDto(previsionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PrevisionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrevisionDTO> findByCriteria(PrevisionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Prevision> specification = createSpecification(criteria);
        return previsionRepository.findAll(specification, page).map(previsionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrevisionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Prevision> specification = createSpecification(criteria);
        return previsionRepository.count(specification);
    }

    /**
     * Function to convert {@link PrevisionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Prevision> createSpecification(PrevisionCriteria criteria) {
        Specification<Prevision> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Prevision_.id));
            }
            if (criteria.getNbLatrine() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNbLatrine(), Prevision_.nbLatrine));
            }
            if (criteria.getNbPuisard() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNbPuisard(), Prevision_.nbPuisard));
            }
            if (criteria.getNbPublic() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNbPublic(), Prevision_.nbPublic));
            }
            if (criteria.getNbScolaire() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNbScolaire(), Prevision_.nbScolaire));
            }
            if (criteria.getAnneeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAnneeId(), root -> root.join(Prevision_.annee, JoinType.LEFT).get(Annee_.id))
                    );
            }
            if (criteria.getCentreId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCentreId(), root -> root.join(Prevision_.centre, JoinType.LEFT).get(Centre_.id))
                    );
            }
            if (criteria.getFicheSuiviOuvrageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFicheSuiviOuvrageId(),
                            root -> root.join(Prevision_.ficheSuiviOuvrages, JoinType.LEFT).get(FicheSuiviOuvrage_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
