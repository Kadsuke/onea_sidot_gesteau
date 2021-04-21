package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.SourceApprovEp;
import com.sidot.gesteau.repository.SourceApprovEpRepository;
import com.sidot.gesteau.repository.search.SourceApprovEpSearchRepository;
import com.sidot.gesteau.service.criteria.SourceApprovEpCriteria;
import com.sidot.gesteau.service.dto.SourceApprovEpDTO;
import com.sidot.gesteau.service.mapper.SourceApprovEpMapper;
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
 * Service for executing complex queries for {@link SourceApprovEp} entities in the database.
 * The main input is a {@link SourceApprovEpCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SourceApprovEpDTO} or a {@link Page} of {@link SourceApprovEpDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SourceApprovEpQueryService extends QueryService<SourceApprovEp> {

    private final Logger log = LoggerFactory.getLogger(SourceApprovEpQueryService.class);

    private final SourceApprovEpRepository sourceApprovEpRepository;

    private final SourceApprovEpMapper sourceApprovEpMapper;

    private final SourceApprovEpSearchRepository sourceApprovEpSearchRepository;

    public SourceApprovEpQueryService(
        SourceApprovEpRepository sourceApprovEpRepository,
        SourceApprovEpMapper sourceApprovEpMapper,
        SourceApprovEpSearchRepository sourceApprovEpSearchRepository
    ) {
        this.sourceApprovEpRepository = sourceApprovEpRepository;
        this.sourceApprovEpMapper = sourceApprovEpMapper;
        this.sourceApprovEpSearchRepository = sourceApprovEpSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SourceApprovEpDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SourceApprovEpDTO> findByCriteria(SourceApprovEpCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SourceApprovEp> specification = createSpecification(criteria);
        return sourceApprovEpMapper.toDto(sourceApprovEpRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SourceApprovEpDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SourceApprovEpDTO> findByCriteria(SourceApprovEpCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SourceApprovEp> specification = createSpecification(criteria);
        return sourceApprovEpRepository.findAll(specification, page).map(sourceApprovEpMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SourceApprovEpCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SourceApprovEp> specification = createSpecification(criteria);
        return sourceApprovEpRepository.count(specification);
    }

    /**
     * Function to convert {@link SourceApprovEpCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SourceApprovEp> createSpecification(SourceApprovEpCriteria criteria) {
        Specification<SourceApprovEp> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SourceApprovEp_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), SourceApprovEp_.libelle));
            }
            if (criteria.getFicheSuiviOuvrageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFicheSuiviOuvrageId(),
                            root -> root.join(SourceApprovEp_.ficheSuiviOuvrages, JoinType.LEFT).get(FicheSuiviOuvrage_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
