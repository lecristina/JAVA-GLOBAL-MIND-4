# Configuração de Banco de Dados

## Configuração Padrão (H2 - Desenvolvimento)

Por padrão, a aplicação usa **H2** (banco em arquivo) para desenvolvimento local. Os dados são salvos em `./data/nexusdb.mv.db`.

## Usar Oracle

### Opção 1: Usando Perfil de Produção

Execute a aplicação com o perfil `prod`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Ou defina a variável de ambiente:
```bash
set SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

### Opção 2: Usando Variáveis de Ambiente

Defina as seguintes variáveis de ambiente:

```bash
set DB_URL=jdbc:oracle:thin:@//br.com.fiap.oracle:1521/ORCL
set DB_USERNAME=rm555241
set DB_PASSWORD=230205
set DB_DRIVER=oracle.jdbc.OracleDriver
set HIBERNATE_DIALECT=org.hibernate.dialect.OracleDialect
set JPA_DDL_AUTO=validate
set H2_CONSOLE_ENABLED=false
```

### Formatos de URL Oracle Suportados

Se o formato padrão não funcionar, tente:

1. **Formato com Service Name:**
   ```
   jdbc:oracle:thin:@//br.com.fiap.oracle:1521/ORCL
   ```

2. **Formato com SID:**
   ```
   jdbc:oracle:thin:@br.com.fiap.oracle:1521:ORCL
   ```

3. **Formato TNS (se tiver tnsnames.ora):**
   ```
   jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=br.com.fiap.oracle)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=ORCL)))
   ```

## Verificar Conexão

Se o host Oracle não estiver acessível, você verá o erro:
```
ORA-17868: Host desconhecido especificado
```

**Soluções:**
1. Verifique se você está conectado à VPN necessária
2. Verifique se o host está correto
3. Use H2 para desenvolvimento local (padrão)
4. Configure um túnel SSH se necessário

## H2 Console

Quando usando H2, você pode acessar o console em:
```
http://localhost:8080/h2-console
```

- JDBC URL: `jdbc:h2:file:./data/nexusdb`
- Usuário: `sa`
- Senha: (vazio)

