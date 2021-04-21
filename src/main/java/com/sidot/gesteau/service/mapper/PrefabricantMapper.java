package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.PrefabricantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Prefabricant} and its DTO {@link PrefabricantDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PrefabricantMapper extends EntityMapper<PrefabricantDTO, Prefabricant> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PrefabricantDTO toDtoId(Prefabricant prefabricant);
}
