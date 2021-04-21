package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.CentreRegroupement;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CentreRegroupement} entity.
 */
public interface CentreRegroupementSearchRepository extends ElasticsearchRepository<CentreRegroupement, Long> {}
