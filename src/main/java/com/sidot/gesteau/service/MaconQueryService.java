package com.sidot.gesteau.service;

import com.sidot.gesteau.domain.*; // for static metamodels
import com.sidot.gesteau.domain.Macon;
import com.sidot.gesteau.repository.MaconRepository;
import com.sidot.gesteau.repository.search.MaconSearchRepository;
import com.sidot.gesteau.service.criteria.MaconCriteria;
import com.sidot.gesteau.service.dto.MaconDTO;
import com.sidot.gesteau.service.mapper.MaconMapper;
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
 * Service for executing complex queries for {@link Macon} entities in the database.
 * The main input is a {@link MaconCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MaconDTO} or a {@link Page} of {@link MaconDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MaconQueryService extends QueryService<Macon> {

    private final Logger log = LoggerFactory.getLogger(MaconQueryService.class);

    private final MaconRepository maconRepository;

    private final MaconMapper maconMapper;

    private final MaconSearchRepository maconSearchRepository;

    public MaconQueryService(MaconRepository maconRepository, MaconMapper maconMapper, MaconSearchRepository maconSearchRepository) {
        this.maconRepository = maconRepository;
        this.maconMapper = maconMapper;
        this.maconSearchRepository = maconSearchRepository;
    }

    /**
     * Return a {@link List} of {@link MaconDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MaconDTO> findByCriteria(MaconCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Macon> specification = createSpecification(criteria);
        return maconMapper.toDto(maconRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MaconDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MaconDTO> findByCriteria(MaconCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Macon> specification = createSpecification(criteria);
        return maconRepository.findAll(specification, page).map(maconMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MaconCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Macon> specification = createSpecification(criteria);
        return maconRepository.count(specification);
    }

    /**
     * Function to convert {@link MaconCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Macon> createSpecification(MaconCriteria criteria) {
        Specification<Macon> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Macon_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), Macon_.libelle));
            }
            if (criteria.getFicheSuiviOuvrageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFicheSuiviOuvrageId(),
                            root -> root.join(Macon_.ficheSuiviOuvrages, JoinType.LEFT).get(FicheSuiviOuvrage_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
