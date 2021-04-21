package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.SourceApprovEpDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SourceApprovEp} and its DTO {@link SourceApprovEpDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SourceApprovEpMapper extends EntityMapper<SourceApprovEpDTO, SourceApprovEp> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SourceApprovEpDTO toDtoId(SourceApprovEp sourceApprovEp);
}
