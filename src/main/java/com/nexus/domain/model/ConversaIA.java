package com.nexus.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade para armazenar mensagens de conversa com IA
 * Permite manter histórico de conversas para contexto
 */
@Entity
@Table(name = "t_mt_conversas_ia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversaIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversa")
    private Integer idConversa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "data_mensagem", nullable = false)
    private LocalDateTime dataMensagem;

    @Column(name = "tipo_mensagem", nullable = false, length = 20)
    private String tipoMensagem; // "USUARIO" ou "IA"

    @Column(name = "mensagem", nullable = false, length = 2000)
    private String mensagem;

    @Column(name = "id_conversa_pai")
    private Integer idConversaPai; // Para agrupar mensagens da mesma conversa

    @Column(name = "contexto", length = 4000)
    private String contexto; // Contexto adicional da conversa
}







