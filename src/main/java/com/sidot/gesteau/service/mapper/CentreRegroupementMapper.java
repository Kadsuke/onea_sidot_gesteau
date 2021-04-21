package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.CentreRegroupementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CentreRegroupement} and its DTO {@link CentreRegroupementDTO}.
 */
@Mapper(componentModel = "spring", uses = { DirectionRegionaleMapper.class })
public interface CentreRegroupementMapper extends EntityMapper<CentreRegroupementDTO, CentreRegroupement> {
    @Mapping(target = "directionRegionale", source = "directionRegionale", qualifiedByName = "id")
    CentreRegroupementDTO toDto(CentreRegroupement s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CentreRegroupementDTO toDtoId(CentreRegroupement centreRegroupement);
}
