package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.SourceApprovEp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SourceApprovEp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SourceApprovEpRepository extends JpaRepository<SourceApprovEp, Long>, JpaSpecificationExecutor<SourceApprovEp> {}
