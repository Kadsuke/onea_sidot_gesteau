package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.NatureOuvrage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the NatureOuvrage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NatureOuvrageRepository extends JpaRepository<NatureOuvrage, Long>, JpaSpecificationExecutor<NatureOuvrage> {}
