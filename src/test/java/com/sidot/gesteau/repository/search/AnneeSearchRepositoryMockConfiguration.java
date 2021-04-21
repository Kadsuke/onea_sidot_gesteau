package com.sidot.gesteau.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link AnneeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class AnneeSearchRepositoryMockConfiguration {

    @MockBean
    private AnneeSearchRepository mockAnneeSearchRepository;
}
