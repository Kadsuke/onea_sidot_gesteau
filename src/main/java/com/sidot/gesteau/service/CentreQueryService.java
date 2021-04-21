package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.Centre;
import com.sidot.gesteau.repository.CentreRepository;
import com.sidot.gesteau.repository.search.CentreSearchRepository;
import com.sidot.gesteau.service.criteria.CentreCriteria;
import com.sidot.gesteau.service.dto.CentreDTO;
import com.sidot.gesteau.service.mapper.CentreMapper;
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
 * Service for executing complex queries for {@link Centre} entities in the database.
 * The main input is a {@link CentreCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CentreDTO} or a {@link Page} of {@link CentreDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CentreQueryService extends QueryService<Centre> {

    private final Logger log = LoggerFactory.getLogger(CentreQueryService.class);

    private final CentreRepository centreRepository;

    private final CentreMapper centreMapper;

    private final CentreSearchRepository centreSearchRepository;

    public CentreQueryService(CentreRepository centreRepository, CentreMapper centreMapper, CentreSearchRepository centreSearchRepository) {
        this.centreRepository = centreRepository;
        this.centreMapper = centreMapper;
        this.centreSearchRepository = centreSearchRepository;
    }

    /**
     * Return a {@link List} of {@link CentreDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CentreDTO> findByCriteria(CentreCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Centre> specification = createSpecification(criteria);
        return centreMapper.toDto(centreRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CentreDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CentreDTO> findByCriteria(CentreCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Centre> specification = createSpecification(criteria);
        return centreRepository.findAll(specification, page).map(centreMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CentreCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Centre> specification = createSpecification(criteria);
        return centreRepository.count(specification);
    }

    /**
     * Function to convert {@link CentreCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Centre> createSpecification(CentreCriteria criteria) {
        Specification<Centre> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Centre_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), Centre_.libelle));
            }
            if (criteria.getResponsable() != null) {
                specification = specification.and(buildStringSpecification(criteria.getResponsable(), Centre_.responsable));
            }
            if (criteria.getContact() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContact(), Centre_.contact));
            }
            if (criteria.getPrevisionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPrevisionId(),
                            root -> root.join(Centre_.prevision, JoinType.LEFT).get(Prevision_.id)
                        )
                    );
            }
            if (criteria.getCentreRegroupementId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCentreRegroupementId(),
                            root -> root.join(Centre_.centreRegroupement, JoinType.LEFT).get(CentreRegroupement_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
