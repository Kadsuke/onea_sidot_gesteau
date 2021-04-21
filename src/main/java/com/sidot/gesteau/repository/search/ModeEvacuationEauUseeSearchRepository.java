package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.ModeEvacuationEauUsee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ModeEvacuationEauUsee} entity.
 */
public interface ModeEvacuationEauUseeSearchRepository extends ElasticsearchRepository<ModeEvacuationEauUsee, Long> {}
