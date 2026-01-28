package com.katya.test.productrestassignement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productRestApiOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Order/Product REST API Tech Team");
        contact.setEmail("support@oprep.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Product REST API")
                .version("1.0.0")
                .description("REST API for managing users, orders, and products. Secured with Keycloak OAuth2.")
                .contact(contact)
                .license(license);

        // Define OAuth2 security scheme for Keycloak
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Keycloak OAuth2 Authentication")
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl("http://localhost:8081/realms/product-rest-api/protocol/openid-connect/auth")
                                .tokenUrl("http://localhost:8081/realms/product-rest-api/protocol/openid-connect/token")
                        )
                        .password(new OAuthFlow()
                                .tokenUrl("http://localhost:8081/realms/product-rest-api/protocol/openid-connect/token")
                        )
                );

        // Add security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("keycloak_oauth2");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .components(new Components()
                        .addSecuritySchemes("keycloak_oauth2", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}

