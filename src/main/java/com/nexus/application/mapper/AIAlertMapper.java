package com.nexus.application.mapper;

import com.nexus.application.dto.AlertaIADTO;
import com.nexus.domain.model.AlertaIA;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AIAlertMapper {
    
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "idAlerta", ignore = true)
    AlertaIA toEntity(AlertaIADTO dto);
    
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    AlertaIADTO toDTO(AlertaIA entity);
}


