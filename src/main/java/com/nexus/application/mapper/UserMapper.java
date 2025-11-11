package com.nexus.application.mapper;

import com.nexus.application.dto.UsuarioDTO;
import com.nexus.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "senhaHash", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    Usuario toEntity(UsuarioDTO dto);
    
    @Mapping(target = "senha", ignore = true)
    UsuarioDTO toDTO(Usuario entity);
}

