package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.DirectionRegionale;
import com.sidot.gesteau.repository.DirectionRegionaleRepository;
import com.sidot.gesteau.repository.search.DirectionRegionaleSearchRepository;
import com.sidot.gesteau.service.criteria.DirectionRegionaleCriteria;
import com.sidot.gesteau.service.dto.DirectionRegionaleDTO;
import com.sidot.gesteau.service.mapper.DirectionRegionaleMapper;
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
 * Service for executing complex queries for {@link DirectionRegionale} entities in the database.
 * The main input is a {@link DirectionRegionaleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DirectionRegionaleDTO} or a {@link Page} of {@link DirectionRegionaleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DirectionRegionaleQueryService extends QueryService<DirectionRegionale> {

    private final Logger log = LoggerFactory.getLogger(DirectionRegionaleQueryService.class);

    private final DirectionRegionaleRepository directionRegionaleRepository;

    private final DirectionRegionaleMapper directionRegionaleMapper;

    private final DirectionRegionaleSearchRepository directionRegionaleSearchRepository;

    public DirectionRegionaleQueryService(
        DirectionRegionaleRepository directionRegionaleRepository,
        DirectionRegionaleMapper directionRegionaleMapper,
        DirectionRegionaleSearchRepository directionRegionaleSearchRepository
    ) {
        this.directionRegionaleRepository = directionRegionaleRepository;
        this.directionRegionaleMapper = directionRegionaleMapper;
        this.directionRegionaleSearchRepository = directionRegionaleSearchRepository;
    }

    /**
     * Return a {@link List} of {@link DirectionRegionaleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DirectionRegionaleDTO> findByCriteria(DirectionRegionaleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<DirectionRegionale> specification = createSpecification(criteria);
        return directionRegionaleMapper.toDto(directionRegionaleRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DirectionRegionaleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DirectionRegionaleDTO> findByCriteria(DirectionRegionaleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DirectionRegionale> specification = createSpecification(criteria);
        return directionRegionaleRepository.findAll(specification, page).map(directionRegionaleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DirectionRegionaleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<DirectionRegionale> specification = createSpecification(criteria);
        return directionRegionaleRepository.count(specification);
    }

    /**
     * Function to convert {@link DirectionRegionaleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DirectionRegionale> createSpecification(DirectionRegionaleCriteria criteria) {
        Specification<DirectionRegionale> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), DirectionRegionale_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), DirectionRegionale_.libelle));
            }
            if (criteria.getResponsable() != null) {
                specification = specification.and(buildStringSpecification(criteria.getResponsable(), DirectionRegionale_.responsable));
            }
            if (criteria.getContact() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContact(), DirectionRegionale_.contact));
            }
            if (criteria.getCentreRegroupementId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCentreRegroupementId(),
                            root -> root.join(DirectionRegionale_.centreRegroupements, JoinType.LEFT).get(CentreRegroupement_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
