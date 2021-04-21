package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.DirectionRegionale;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link DirectionRegionale} entity.
 */
public interface DirectionRegionaleSearchRepository extends ElasticsearchRepository<DirectionRegionale, Long> {}
