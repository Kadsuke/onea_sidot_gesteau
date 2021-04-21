package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.CentreRegroupement;
import com.sidot.gesteau.repository.CentreRegroupementRepository;
import com.sidot.gesteau.repository.search.CentreRegroupementSearchRepository;
import com.sidot.gesteau.service.criteria.CentreRegroupementCriteria;
import com.sidot.gesteau.service.dto.CentreRegroupementDTO;
import com.sidot.gesteau.service.mapper.CentreRegroupementMapper;
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
 * Service for executing complex queries for {@link CentreRegroupement} entities in the database.
 * The main input is a {@link CentreRegroupementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CentreRegroupementDTO} or a {@link Page} of {@link CentreRegroupementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CentreRegroupementQueryService extends QueryService<CentreRegroupement> {

    private final Logger log = LoggerFactory.getLogger(CentreRegroupementQueryService.class);

    private final CentreRegroupementRepository centreRegroupementRepository;

    private final CentreRegroupementMapper centreRegroupementMapper;

    private final CentreRegroupementSearchRepository centreRegroupementSearchRepository;

    public CentreRegroupementQueryService(
        CentreRegroupementRepository centreRegroupementRepository,
        CentreRegroupementMapper centreRegroupementMapper,
        CentreRegroupementSearchRepository centreRegroupementSearchRepository
    ) {
        this.centreRegroupementRepository = centreRegroupementRepository;
        this.centreRegroupementMapper = centreRegroupementMapper;
        this.centreRegroupementSearchRepository = centreRegroupementSearchRepository;
    }

    /**
     * Return a {@link List} of {@link CentreRegroupementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CentreRegroupementDTO> findByCriteria(CentreRegroupementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CentreRegroupement> specification = createSpecification(criteria);
        return centreRegroupementMapper.toDto(centreRegroupementRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CentreRegroupementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CentreRegroupementDTO> findByCriteria(CentreRegroupementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CentreRegroupement> specification = createSpecification(criteria);
        return centreRegroupementRepository.findAll(specification, page).map(centreRegroupementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CentreRegroupementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CentreRegroupement> specification = createSpecification(criteria);
        return centreRegroupementRepository.count(specification);
    }

    /**
     * Function to convert {@link CentreRegroupementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CentreRegroupement> createSpecification(CentreRegroupementCriteria criteria) {
        Specification<CentreRegroupement> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CentreRegroupement_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), CentreRegroupement_.libelle));
            }
            if (criteria.getResponsable() != null) {
                specification = specification.and(buildStringSpecification(criteria.getResponsable(), CentreRegroupement_.responsable));
            }
            if (criteria.getContact() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContact(), CentreRegroupement_.contact));
            }
            if (criteria.getCentreId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCentreId(),
                            root -> root.join(CentreRegroupement_.centres, JoinType.LEFT).get(Centre_.id)
                        )
                    );
            }
            if (criteria.getDirectionRegionaleId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDirectionRegionaleId(),
                            root -> root.join(CentreRegroupement_.directionRegionale, JoinType.LEFT).get(DirectionRegionale_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
