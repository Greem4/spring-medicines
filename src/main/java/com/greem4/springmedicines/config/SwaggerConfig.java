package com.greem4.springmedicines.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String LOCAL_PASSWORD_AUTH = "LocalPasswordAuth";
    private static final String YANDEX_OAUTH2 = "YandexOAuth2";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Medicines API")
                        .description("API документация для приложения Spring Medicines")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(LOCAL_PASSWORD_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .password(new OAuthFlow()
                                                        .tokenUrl("/api/auth/login")
                                                        .scopes(new Scopes()
                                                                .addString("read", "Read scope")
                                                                .addString("write", "Write scope")
                                                        )
                                                )
                                        )
                        )
                        .addSecuritySchemes(YANDEX_OAUTH2,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
//                                                        .authorizationUrl("https://oauth.yandex.ru/authorize")
                                                        .authorizationUrl("http://localhost:8080/oauth2/authorization/yandex")
                                                        .tokenUrl("https://oauth.yandex.ru/token")
                                                        .scopes(new Scopes()
                                                                .addString("login:email", "Get user email")
                                                        )
                                                )
                                        )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(LOCAL_PASSWORD_AUTH))
                .addSecurityItem(new SecurityRequirement().addList(YANDEX_OAUTH2));
    }
}
