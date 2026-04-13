package com.cora.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cora API")
                        .description("API REST para criar e listar contas bancárias")
                        .version("0.0.1-SNAPSHOT"));
    }
}
