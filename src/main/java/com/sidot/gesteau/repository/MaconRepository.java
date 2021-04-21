package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.Macon;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Macon entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaconRepository extends JpaRepository<Macon, Long>, JpaSpecificationExecutor<Macon> {}
