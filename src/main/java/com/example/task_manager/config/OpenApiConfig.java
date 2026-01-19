package com.example.task_manager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("1.0.0")
                        .description("""
                                API RESTful de gestion des tâches avec authentification JWT.
                                
                                ## Fonctionnalités principales :
                                - Authentification et gestion des utilisateurs
                                - CRUD complet sur les tâches
                                - Sécurisation par JWT (JSON Web Token)
                                - Validation des données
                                
                                ## Comment utiliser cette API :
                                1. Créez un compte via `/api/auth/register`
                                2. Connectez-vous via `/api/auth/login` pour obtenir un token JWT
                                3. Cliquez sur "Authorize" et entrez : `Bearer <votre_token>`
                                4. Vous pouvez maintenant accéder aux endpoints protégés
                                """)
                        .contact(new Contact()
                                .name("Tchinda Joel")
                                .email("tchinda.joel@example.com")
                                .url("https://github.com/tchinda-joel")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://api.taskmanager.com")
                                .description("Serveur de production")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez votre token JWT précédé de 'Bearer '")
                        )
                );
    }
}