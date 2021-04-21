package com.sidot.gesteau.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link FicheSuiviOuvrageSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class FicheSuiviOuvrageSearchRepositoryMockConfiguration {

    @MockBean
    private FicheSuiviOuvrageSearchRepository mockFicheSuiviOuvrageSearchRepository;
}
