package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.TypeHabitation;
import com.sidot.gesteau.repository.TypeHabitationRepository;
import com.sidot.gesteau.repository.search.TypeHabitationSearchRepository;
import com.sidot.gesteau.service.criteria.TypeHabitationCriteria;
import com.sidot.gesteau.service.dto.TypeHabitationDTO;
import com.sidot.gesteau.service.mapper.TypeHabitationMapper;
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
 * Service for executing complex queries for {@link TypeHabitation} entities in the database.
 * The main input is a {@link TypeHabitationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TypeHabitationDTO} or a {@link Page} of {@link TypeHabitationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TypeHabitationQueryService extends QueryService<TypeHabitation> {

    private final Logger log = LoggerFactory.getLogger(TypeHabitationQueryService.class);

    private final TypeHabitationRepository typeHabitationRepository;

    private final TypeHabitationMapper typeHabitationMapper;

    private final TypeHabitationSearchRepository typeHabitationSearchRepository;

    public TypeHabitationQueryService(
        TypeHabitationRepository typeHabitationRepository,
        TypeHabitationMapper typeHabitationMapper,
        TypeHabitationSearchRepository typeHabitationSearchRepository
    ) {
        this.typeHabitationRepository = typeHabitationRepository;
        this.typeHabitationMapper = typeHabitationMapper;
        this.typeHabitationSearchRepository = typeHabitationSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TypeHabitationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TypeHabitationDTO> findByCriteria(TypeHabitationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TypeHabitation> specification = createSpecification(criteria);
        return typeHabitationMapper.toDto(typeHabitationRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TypeHabitationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TypeHabitationDTO> findByCriteria(TypeHabitationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TypeHabitation> specification = createSpecification(criteria);
        return typeHabitationRepository.findAll(specification, page).map(typeHabitationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TypeHabitationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TypeHabitation> specification = createSpecification(criteria);
        return typeHabitationRepository.count(specification);
    }

    /**
     * Function to convert {@link TypeHabitationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TypeHabitation> createSpecification(TypeHabitationCriteria criteria) {
        Specification<TypeHabitation> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TypeHabitation_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), TypeHabitation_.libelle));
            }
            if (criteria.getFicheSuiviOuvrageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFicheSuiviOuvrageId(),
                            root -> root.join(TypeHabitation_.ficheSuiviOuvrages, JoinType.LEFT).get(FicheSuiviOuvrage_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
