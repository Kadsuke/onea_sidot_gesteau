package com.sidot.gesteau.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrevisionMapperTest {

    private PrevisionMapper previsionMapper;

    @BeforeEach
    public void setUp() {
        previsionMapper = new PrevisionMapperImpl();
    }
}
