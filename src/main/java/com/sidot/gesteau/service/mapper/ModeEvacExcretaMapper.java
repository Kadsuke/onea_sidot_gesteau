package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.ModeEvacExcretaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ModeEvacExcreta} and its DTO {@link ModeEvacExcretaDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ModeEvacExcretaMapper extends EntityMapper<ModeEvacExcretaDTO, ModeEvacExcreta> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ModeEvacExcretaDTO toDtoId(ModeEvacExcreta modeEvacExcreta);
}
