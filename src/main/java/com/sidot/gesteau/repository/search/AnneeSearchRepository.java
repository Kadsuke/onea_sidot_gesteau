package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.Annee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Annee} entity.
 */
public interface AnneeSearchRepository extends ElasticsearchRepository<Annee, Long> {}
