package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.Annee;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Annee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnneeRepository extends JpaRepository<Annee, Long>, JpaSpecificationExecutor<Annee> {}
