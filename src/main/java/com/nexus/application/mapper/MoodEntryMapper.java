package com.nexus.application.mapper;

import com.nexus.application.dto.HumorDTO;
import com.nexus.domain.model.Humor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MoodEntryMapper {
    
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "idHumor", ignore = true)
    Humor toEntity(HumorDTO dto);
    
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    HumorDTO toDTO(Humor entity);
}


