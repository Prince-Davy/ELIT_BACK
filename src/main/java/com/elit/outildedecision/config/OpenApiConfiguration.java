package com.elit.outildedecision.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfiguration {
    /**
     * Creates and returns a custom instance of OpenAPI with a security scheme based on the JWT token.
     * @return An instance of OpenAPI with a configured security scheme.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }

    /**
     * Customize API operations with specific security requirements.
     * @return An instance of OperationCustomizer that adds security requirements to operations.
     */
    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            return operation;
        };
    }
}
