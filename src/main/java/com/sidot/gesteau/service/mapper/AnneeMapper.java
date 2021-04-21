package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.AnneeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Annee} and its DTO {@link AnneeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AnneeMapper extends EntityMapper<AnneeDTO, Annee> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AnneeDTO toDtoId(Annee annee);
}
