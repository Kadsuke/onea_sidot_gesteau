package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.MaconDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Macon} and its DTO {@link MaconDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MaconMapper extends EntityMapper<MaconDTO, Macon> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MaconDTO toDtoId(Macon macon);
}
