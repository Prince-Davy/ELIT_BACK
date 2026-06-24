package com.elit.outildedecision.shared.config;

import com.elit.outildedecision.shared.exception.ErrorMessage;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

/** Open api configuration class. */
@Configuration
public class OpenApiConfiguration {

    /** Reference to the shared error payload schema declared in the components. */
    private static final String ERROR_SCHEMA_REF = "#/components/schemas/ErrorMessage";

    /**
     * Creates and returns a custom instance of OpenAPI with a security scheme based on the JWT token
     * and the shared {@link ErrorMessage} schema so the common error responses can reference it.
     * @return An instance of OpenAPI with a configured security scheme and error schema.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        Schema<?> errorSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(ErrorMessage.class)).schema;
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                        .addSchemas("ErrorMessage", errorSchema));
    }

    /**
     * Customize API operations with the transverse security requirement and the common error
     * responses, so individual controllers don't repeat (or forget) them.
     * @return An instance of OperationCustomizer that enriches every operation.
     */
    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            addCommonErrorResponses(operation.getResponses());
            return operation;
        };
    }

    /**
     * Adds the transverse error responses to an operation without overriding the ones already
     * derived from the handler (notably the 2xx coming from the return type). Each response carries
     * the shared {@link ErrorMessage} JSON schema so the contract exposes the error body shape.
     * @param responses the operation responses to enrich
     */
    private void addCommonErrorResponses(final ApiResponses responses) {
        responses.putIfAbsent("400", errorResponse("Bad Request"));
        responses.putIfAbsent("401", errorResponse("Unauthorized"));
        responses.putIfAbsent("403", errorResponse("Forbidden"));
        responses.putIfAbsent("404", errorResponse("Not Found"));
        responses.putIfAbsent("409", errorResponse("Conflict"));
        responses.putIfAbsent("500", errorResponse("Internal Server Error"));
    }

    /**
     * Builds a common error response carrying the shared {@link ErrorMessage} JSON schema.
     * @param description the human-readable response description
     * @return an ApiResponse with an {@code application/json} {@link ErrorMessage} body
     */
    private ApiResponse errorResponse(final String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType()
                                .schema(new Schema<>().$ref(ERROR_SCHEMA_REF))));
    }
}
