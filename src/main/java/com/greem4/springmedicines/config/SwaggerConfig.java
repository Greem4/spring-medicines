package com.greem4.springmedicines.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BASIC_AUTH = "авторизация";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Medicines API")
                        .description("API документация для приложения Spring Medicines")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BASIC_AUTH, new SecurityScheme()
                                .name(BASIC_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}
