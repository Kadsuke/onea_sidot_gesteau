package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.PrevisionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Prevision} and its DTO {@link PrevisionDTO}.
 */
@Mapper(componentModel = "spring", uses = { AnneeMapper.class, CentreMapper.class })
public interface PrevisionMapper extends EntityMapper<PrevisionDTO, Prevision> {
    @Mapping(target = "annee", source = "annee", qualifiedByName = "id")
    @Mapping(target = "centre", source = "centre", qualifiedByName = "id")
    PrevisionDTO toDto(Prevision s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PrevisionDTO toDtoId(Prevision prevision);
}
