package com.nexus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MindTrack / Nexus API")
                        .version("1.0.0")
                        .description("API para monitoramento de saúde mental e produtividade no trabalho de TI")
                        .contact(new Contact()
                                .name("Nexus Team")
                                .email("nexus@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("⚠️ IMPORTANTE: Cole APENAS o token JWT (sem a palavra 'Bearer'). Exemplo: eyJhbGciOiJIUzM4NCJ9... O Swagger adiciona 'Bearer' automaticamente!")))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url("http://localhost:8080")
                        .description("Servidor Local"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("nexus-api")
                .pathsToMatch("/api/**")
                .build();
    }
}

