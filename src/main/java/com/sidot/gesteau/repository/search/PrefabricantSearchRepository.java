package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.Prefabricant;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Prefabricant} entity.
 */
public interface PrefabricantSearchRepository extends ElasticsearchRepository<Prefabricant, Long> {}
