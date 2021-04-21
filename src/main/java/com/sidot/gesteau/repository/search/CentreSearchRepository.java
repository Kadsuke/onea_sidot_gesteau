package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.Centre;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Centre} entity.
 */
public interface CentreSearchRepository extends ElasticsearchRepository<Centre, Long> {}
