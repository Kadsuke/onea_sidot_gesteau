package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.CentreDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Centre} and its DTO {@link CentreDTO}.
 */
@Mapper(componentModel = "spring", uses = { CentreRegroupementMapper.class })
public interface CentreMapper extends EntityMapper<CentreDTO, Centre> {
    @Mapping(target = "centreRegroupement", source = "centreRegroupement", qualifiedByName = "id")
    CentreDTO toDto(Centre s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CentreDTO toDtoId(Centre centre);
}
