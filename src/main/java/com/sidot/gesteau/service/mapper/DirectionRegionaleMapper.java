package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.DirectionRegionaleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DirectionRegionale} and its DTO {@link DirectionRegionaleDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DirectionRegionaleMapper extends EntityMapper<DirectionRegionaleDTO, DirectionRegionale> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DirectionRegionaleDTO toDtoId(DirectionRegionale directionRegionale);
}
