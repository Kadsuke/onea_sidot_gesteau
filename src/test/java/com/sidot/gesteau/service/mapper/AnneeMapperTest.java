package com.sidot.gesteau.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnneeMapperTest {

    private AnneeMapper anneeMapper;

    @BeforeEach
    public void setUp() {
        anneeMapper = new AnneeMapperImpl();
    }
}
