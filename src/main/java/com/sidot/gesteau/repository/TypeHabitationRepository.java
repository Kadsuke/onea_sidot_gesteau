package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.TypeHabitation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TypeHabitation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeHabitationRepository extends JpaRepository<TypeHabitation, Long>, JpaSpecificationExecutor<TypeHabitation> {}
