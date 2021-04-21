package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.TypeHabitationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TypeHabitation} and its DTO {@link TypeHabitationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TypeHabitationMapper extends EntityMapper<TypeHabitationDTO, TypeHabitation> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TypeHabitationDTO toDtoId(TypeHabitation typeHabitation);
}
