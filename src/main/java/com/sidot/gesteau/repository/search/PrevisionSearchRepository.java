package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.Prevision;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Prevision} entity.
 */
public interface PrevisionSearchRepository extends ElasticsearchRepository<Prevision, Long> {}
