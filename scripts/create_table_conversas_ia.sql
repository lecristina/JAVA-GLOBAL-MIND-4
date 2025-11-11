-- ============================================================================
-- Script SQL para criar a tabela t_mt_conversas_ia
-- Sistema: Nexus - Mind Track
-- Descrição: Tabela para armazenar mensagens de conversa com IA
-- ============================================================================

-- Remover índices se existirem
BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_conversa_data';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_conversa_pai';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_conversa_usuario';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

-- Remover constraints se existirem
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE t_mt_conversas_ia DROP CONSTRAINT fk_conversa_usuario';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

-- Remover tabela se existir
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE t_mt_conversas_ia CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

-- Criar tabela
CREATE TABLE t_mt_conversas_ia (
    id_conversa NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario NUMBER NOT NULL,
    data_mensagem TIMESTAMP NOT NULL,
    tipo_mensagem VARCHAR2(20) NOT NULL,
    mensagem VARCHAR2(2000) NOT NULL,
    id_conversa_pai NUMBER,
    contexto VARCHAR2(4000),
    CONSTRAINT fk_conversa_usuario FOREIGN KEY (id_usuario) 
        REFERENCES t_mt_usuarios(id_usuario)
);

-- Criar índices para melhorar performance nas consultas
CREATE INDEX idx_conversa_usuario ON t_mt_conversas_ia(id_usuario);
CREATE INDEX idx_conversa_pai ON t_mt_conversas_ia(id_conversa_pai);
CREATE INDEX idx_conversa_data ON t_mt_conversas_ia(data_mensagem);

-- Adicionar comentários na tabela e colunas
COMMENT ON TABLE t_mt_conversas_ia IS 'Tabela para armazenar mensagens de conversa com IA';
COMMENT ON COLUMN t_mt_conversas_ia.id_conversa IS 'Identificador único da conversa';
COMMENT ON COLUMN t_mt_conversas_ia.id_usuario IS 'Referência ao usuário';
COMMENT ON COLUMN t_mt_conversas_ia.data_mensagem IS 'Data e hora da mensagem';
COMMENT ON COLUMN t_mt_conversas_ia.tipo_mensagem IS 'Tipo: USUARIO ou IA';
COMMENT ON COLUMN t_mt_conversas_ia.mensagem IS 'Conteúdo da mensagem';
COMMENT ON COLUMN t_mt_conversas_ia.id_conversa_pai IS 'ID da conversa pai para agrupar mensagens';
COMMENT ON COLUMN t_mt_conversas_ia.contexto IS 'Contexto adicional da conversa';

-- Confirmar criação
SELECT 'Tabela t_mt_conversas_ia criada com sucesso!' AS STATUS FROM DUAL;

