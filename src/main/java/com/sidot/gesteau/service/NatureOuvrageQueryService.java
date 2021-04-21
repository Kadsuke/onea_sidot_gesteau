package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.NatureOuvrage;
import com.sidot.gesteau.repository.NatureOuvrageRepository;
import com.sidot.gesteau.repository.search.NatureOuvrageSearchRepository;
import com.sidot.gesteau.service.criteria.NatureOuvrageCriteria;
import com.sidot.gesteau.service.dto.NatureOuvrageDTO;
import com.sidot.gesteau.service.mapper.NatureOuvrageMapper;
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
 * Service for executing complex queries for {@link NatureOuvrage} entities in the database.
 * The main input is a {@link NatureOuvrageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link NatureOuvrageDTO} or a {@link Page} of {@link NatureOuvrageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NatureOuvrageQueryService extends QueryService<NatureOuvrage> {

    private final Logger log = LoggerFactory.getLogger(NatureOuvrageQueryService.class);

    private final NatureOuvrageRepository natureOuvrageRepository;

    private final NatureOuvrageMapper natureOuvrageMapper;

    private final NatureOuvrageSearchRepository natureOuvrageSearchRepository;

    public NatureOuvrageQueryService(
        NatureOuvrageRepository natureOuvrageRepository,
        NatureOuvrageMapper natureOuvrageMapper,
        NatureOuvrageSearchRepository natureOuvrageSearchRepository
    ) {
        this.natureOuvrageRepository = natureOuvrageRepository;
        this.natureOuvrageMapper = natureOuvrageMapper;
        this.natureOuvrageSearchRepository = natureOuvrageSearchRepository;
    }

    /**
     * Return a {@link List} of {@link NatureOuvrageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<NatureOuvrageDTO> findByCriteria(NatureOuvrageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<NatureOuvrage> specification = createSpecification(criteria);
        return natureOuvrageMapper.toDto(natureOuvrageRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link NatureOuvrageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NatureOuvrageDTO> findByCriteria(NatureOuvrageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<NatureOuvrage> specification = createSpecification(criteria);
        return natureOuvrageRepository.findAll(specification, page).map(natureOuvrageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NatureOuvrageCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<NatureOuvrage> specification = createSpecification(criteria);
        return natureOuvrageRepository.count(specification);
    }

    /**
     * Function to convert {@link NatureOuvrageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<NatureOuvrage> createSpecification(NatureOuvrageCriteria criteria) {
        Specification<NatureOuvrage> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), NatureOuvrage_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), NatureOuvrage_.libelle));
            }
            if (criteria.getFicheSuiviOuvrageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFicheSuiviOuvrageId(),
                            root -> root.join(NatureOuvrage_.ficheSuiviOuvrages, JoinType.LEFT).get(FicheSuiviOuvrage_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
