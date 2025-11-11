package com.nexus.application.mapper;

import com.nexus.application.dto.HabitoDTO;
import com.nexus.domain.model.Habito;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HabitMapper {
    
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "idHabito", ignore = true)
    Habito toEntity(HabitoDTO dto);
    
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    HabitoDTO toDTO(Habito entity);
}


