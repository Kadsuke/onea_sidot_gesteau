package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.NatureOuvrageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NatureOuvrage} and its DTO {@link NatureOuvrageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface NatureOuvrageMapper extends EntityMapper<NatureOuvrageDTO, NatureOuvrage> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NatureOuvrageDTO toDtoId(NatureOuvrage natureOuvrage);
}
