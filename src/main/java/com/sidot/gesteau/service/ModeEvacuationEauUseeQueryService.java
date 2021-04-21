package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.ModeEvacuationEauUsee;
import com.sidot.gesteau.repository.ModeEvacuationEauUseeRepository;
import com.sidot.gesteau.repository.search.ModeEvacuationEauUseeSearchRepository;
import com.sidot.gesteau.service.criteria.ModeEvacuationEauUseeCriteria;
import com.sidot.gesteau.service.dto.ModeEvacuationEauUseeDTO;
import com.sidot.gesteau.service.mapper.ModeEvacuationEauUseeMapper;
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
 * Service for executing complex queries for {@link ModeEvacuationEauUsee} entities in the database.
 * The main input is a {@link ModeEvacuationEauUseeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ModeEvacuationEauUseeDTO} or a {@link Page} of {@link ModeEvacuationEauUseeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ModeEvacuationEauUseeQueryService extends QueryService<ModeEvacuationEauUsee> {

    private final Logger log = LoggerFactory.getLogger(ModeEvacuationEauUseeQueryService.class);

    private final ModeEvacuationEauUseeRepository modeEvacuationEauUseeRepository;

    private final ModeEvacuationEauUseeMapper modeEvacuationEauUseeMapper;

    private final ModeEvacuationEauUseeSearchRepository modeEvacuationEauUseeSearchRepository;

    public ModeEvacuationEauUseeQueryService(
        ModeEvacuationEauUseeRepository modeEvacuationEauUseeRepository,
        ModeEvacuationEauUseeMapper modeEvacuationEauUseeMapper,
        ModeEvacuationEauUseeSearchRepository modeEvacuationEauUseeSearchRepository
    ) {
        this.modeEvacuationEauUseeRepository = modeEvacuationEauUseeRepository;
        this.modeEvacuationEauUseeMapper = modeEvacuationEauUseeMapper;
        this.modeEvacuationEauUseeSearchRepository = modeEvacuationEauUseeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ModeEvacuationEauUseeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ModeEvacuationEauUseeDTO> findByCriteria(ModeEvacuationEauUseeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ModeEvacuationEauUsee> specification = createSpecification(criteria);
        return modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUseeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ModeEvacuationEauUseeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ModeEvacuationEauUseeDTO> findByCriteria(ModeEvacuationEauUseeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ModeEvacuationEauUsee> specification = createSpecification(criteria);
        return modeEvacuationEauUseeRepository.findAll(specification, page).map(modeEvacuationEauUseeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ModeEvacuationEauUseeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ModeEvacuationEauUsee> specification = createSpecification(criteria);
        return modeEvacuationEauUseeRepository.count(specification);
    }

    /**
     * Function to convert {@link ModeEvacuationEauUseeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ModeEvacuationEauUsee> createSpecification(ModeEvacuationEauUseeCriteria criteria) {
        Specification<ModeEvacuationEauUsee> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ModeEvacuationEauUsee_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), ModeEvacuationEauUsee_.libelle));
            }
            if (criteria.getFicheSuiviOuvrageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFicheSuiviOuvrageId(),
                            root -> root.join(ModeEvacuationEauUsee_.ficheSuiviOuvrages, JoinType.LEFT).get(FicheSuiviOuvrage_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
