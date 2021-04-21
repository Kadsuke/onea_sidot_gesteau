package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.Prefabricant;
import com.sidot.gesteau.repository.PrefabricantRepository;
import com.sidot.gesteau.repository.search.PrefabricantSearchRepository;
import com.sidot.gesteau.service.criteria.PrefabricantCriteria;
import com.sidot.gesteau.service.dto.PrefabricantDTO;
import com.sidot.gesteau.service.mapper.PrefabricantMapper;
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
 * Service for executing complex queries for {@link Prefabricant} entities in the database.
 * The main input is a {@link PrefabricantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrefabricantDTO} or a {@link Page} of {@link PrefabricantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrefabricantQueryService extends QueryService<Prefabricant> {

    private final Logger log = LoggerFactory.getLogger(PrefabricantQueryService.class);

    private final PrefabricantRepository prefabricantRepository;

    private final PrefabricantMapper prefabricantMapper;

    private final PrefabricantSearchRepository prefabricantSearchRepository;

    public PrefabricantQueryService(
        PrefabricantRepository prefabricantRepository,
        PrefabricantMapper prefabricantMapper,
        PrefabricantSearchRepository prefabricantSearchRepository
    ) {
        this.prefabricantRepository = prefabricantRepository;
        this.prefabricantMapper = prefabricantMapper;
        this.prefabricantSearchRepository = prefabricantSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PrefabricantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrefabricantDTO> findByCriteria(PrefabricantCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Prefabricant> specification = createSpecification(criteria);
        return prefabricantMapper.toDto(prefabricantRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PrefabricantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrefabricantDTO> findByCriteria(PrefabricantCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Prefabricant> specification = createSpecification(criteria);
        return prefabricantRepository.findAll(specification, page).map(prefabricantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrefabricantCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Prefabricant> specification = createSpecification(criteria);
        return prefabricantRepository.count(specification);
    }

    /**
     * Function to convert {@link PrefabricantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Prefabricant> createSpecification(PrefabricantCriteria criteria) {
        Specification<Prefabricant> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Prefabricant_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), Prefabricant_.libelle));
            }
            if (criteria.getFicheSuiviOuvrageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFicheSuiviOuvrageId(),
                            root -> root.join(Prefabricant_.ficheSuiviOuvrages, JoinType.LEFT).get(FicheSuiviOuvrage_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
