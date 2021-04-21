package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.ModeEvacExcreta;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ModeEvacExcreta} entity.
 */
public interface ModeEvacExcretaSearchRepository extends ElasticsearchRepository<ModeEvacExcreta, Long> {}
