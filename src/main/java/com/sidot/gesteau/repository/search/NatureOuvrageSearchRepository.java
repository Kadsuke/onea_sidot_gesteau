package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.NatureOuvrage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link NatureOuvrage} entity.
 */
public interface NatureOuvrageSearchRepository extends ElasticsearchRepository<NatureOuvrage, Long> {}
