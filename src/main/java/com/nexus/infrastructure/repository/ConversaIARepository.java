package com.nexus.infrastructure.repository;

import com.nexus.domain.model.ConversaIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversaIARepository extends JpaRepository<ConversaIA, Integer> {
    
    /**
     * Busca últimas mensagens de uma conversa específica
     */
    List<ConversaIA> findByUsuario_IdUsuarioAndIdConversaPaiOrderByDataMensagemAsc(
            Integer idUsuario, Integer idConversaPai);
    
    /**
     * Busca últimas mensagens do usuário (para contexto)
     */
    List<ConversaIA> findByUsuario_IdUsuarioOrderByDataMensagemDesc(
            Integer idUsuario);
    
    /**
     * Busca mensagens recentes para contexto
     */
    List<ConversaIA> findByUsuario_IdUsuarioAndDataMensagemAfterOrderByDataMensagemAsc(
            Integer idUsuario, LocalDateTime dataInicio);
    
    /**
     * Busca última conversa do usuário
     */
    ConversaIA findFirstByUsuario_IdUsuarioOrderByDataMensagemDesc(Integer idUsuario);
}







