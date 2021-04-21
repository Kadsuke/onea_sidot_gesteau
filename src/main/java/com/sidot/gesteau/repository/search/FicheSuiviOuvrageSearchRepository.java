package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.FicheSuiviOuvrage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link FicheSuiviOuvrage} entity.
 */
public interface FicheSuiviOuvrageSearchRepository extends ElasticsearchRepository<FicheSuiviOuvrage, Long> {}
