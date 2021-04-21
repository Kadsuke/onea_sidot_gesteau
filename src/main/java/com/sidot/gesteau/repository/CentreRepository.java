package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.Centre;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Centre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CentreRepository extends JpaRepository<Centre, Long>, JpaSpecificationExecutor<Centre> {}
