package com.example.ecommerceapp.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigs {

    @Bean
    public OpenAPI advertisementOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Ecommerce Application API")
                        .description("API documentation for Ecommerce")
                        .version("v1.0")
                        .contact(new Contact().name("Tunahan Can").email("tunahan.can@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Ecommerce API Documentation")
                        .url("https://github.com/TunahanCan"));
    }
}
