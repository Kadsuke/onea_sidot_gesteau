package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.Prevision;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Prevision entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrevisionRepository extends JpaRepository<Prevision, Long>, JpaSpecificationExecutor<Prevision> {}
