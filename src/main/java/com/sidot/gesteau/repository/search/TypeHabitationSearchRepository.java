package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.TypeHabitation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TypeHabitation} entity.
 */
public interface TypeHabitationSearchRepository extends ElasticsearchRepository<TypeHabitation, Long> {}
