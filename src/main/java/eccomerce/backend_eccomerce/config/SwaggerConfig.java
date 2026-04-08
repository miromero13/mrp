package eccomerce.backend_eccomerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig { 

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearer-key", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info()
                .title("Eccomerce API")
                .version("0.1.0") 
                .description("API para el ecommerce"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .paths(new Paths()
                // .addPathItem("/client/auth/logout", new PathItem())
                .addPathItem("/auth/login", new PathItem()))
                ;
    }
}
