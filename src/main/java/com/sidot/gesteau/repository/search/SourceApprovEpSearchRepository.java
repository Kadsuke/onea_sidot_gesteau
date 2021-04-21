package com.sidot.gesteau.repository.search;

import com.sidot.gesteau.domain.SourceApprovEp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link SourceApprovEp} entity.
 */
public interface SourceApprovEpSearchRepository extends ElasticsearchRepository<SourceApprovEp, Long> {}
