package com.nexus.application.mapper;

import com.nexus.application.dto.SprintDTO;
import com.nexus.domain.model.Sprint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SprintMapper {
    
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "idSprint", ignore = true)
    Sprint toEntity(SprintDTO dto);
    
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    SprintDTO toDTO(Sprint entity);
}


