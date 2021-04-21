package com.sidot.gesteau.repository;

import com.sidot.gesteau.domain.Prefabricant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Prefabricant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrefabricantRepository extends JpaRepository<Prefabricant, Long>, JpaSpecificationExecutor<Prefabricant> {}
