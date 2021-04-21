package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.ModeEvacuationEauUsee;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ModeEvacuationEauUsee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ModeEvacuationEauUseeRepository
    extends JpaRepository<ModeEvacuationEauUsee, Long>, JpaSpecificationExecutor<ModeEvacuationEauUsee> {}
