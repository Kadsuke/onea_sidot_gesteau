package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.Macon;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Macon} entity.
 */
public interface MaconSearchRepository extends ElasticsearchRepository<Macon, Long> {}
