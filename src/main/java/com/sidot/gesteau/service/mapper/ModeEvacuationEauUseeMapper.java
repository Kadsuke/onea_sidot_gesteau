package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.ModeEvacuationEauUseeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ModeEvacuationEauUsee} and its DTO {@link ModeEvacuationEauUseeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ModeEvacuationEauUseeMapper extends EntityMapper<ModeEvacuationEauUseeDTO, ModeEvacuationEauUsee> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ModeEvacuationEauUseeDTO toDtoId(ModeEvacuationEauUsee modeEvacuationEauUsee);
}
