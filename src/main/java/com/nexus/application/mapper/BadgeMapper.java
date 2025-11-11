package com.nexus.application.mapper;

import com.nexus.application.dto.BadgeDTO;
import com.nexus.domain.model.Badge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BadgeMapper {
    
    @Mapping(target = "idBadge", ignore = true)
    Badge toEntity(BadgeDTO dto);
    
    BadgeDTO toDTO(Badge entity);
}


