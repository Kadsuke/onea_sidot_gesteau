package com.sidot.gesteau.service.mapper;

import com.sidot.gesteau.domain.*;
import com.sidot.gesteau.service.dto.FicheSuiviOuvrageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FicheSuiviOuvrage} and its DTO {@link FicheSuiviOuvrageDTO}.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        PrevisionMapper.class,
        NatureOuvrageMapper.class,
        TypeHabitationMapper.class,
        SourceApprovEpMapper.class,
        ModeEvacuationEauUseeMapper.class,
        ModeEvacExcretaMapper.class,
        MaconMapper.class,
        PrefabricantMapper.class,
    }
)
public interface FicheSuiviOuvrageMapper extends EntityMapper<FicheSuiviOuvrageDTO, FicheSuiviOuvrage> {
    @Mapping(target = "prevision", source = "prevision", qualifiedByName = "id")
    @Mapping(target = "natureouvrage", source = "natureouvrage", qualifiedByName = "id")
    @Mapping(target = "typehabitation", source = "typehabitation", qualifiedByName = "id")
    @Mapping(target = "sourceapprovep", source = "sourceapprovep", qualifiedByName = "id")
    @Mapping(target = "modeevacuationeauusee", source = "modeevacuationeauusee", qualifiedByName = "id")
    @Mapping(target = "modeevacexcreta", source = "modeevacexcreta", qualifiedByName = "id")
    @Mapping(target = "macon", source = "macon", qualifiedByName = "id")
    @Mapping(target = "prefabricant", source = "prefabricant", qualifiedByName = "id")
    FicheSuiviOuvrageDTO toDto(FicheSuiviOuvrage s);
}
