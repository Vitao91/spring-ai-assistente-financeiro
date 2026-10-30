package com.dio.springai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Assistente Financeiro com Spring AI e Voz")
                        .version("1.0.0")
                        .description("API Inteligente para gestão de orçamento pessoal e transações financeiras " +
                                "utilizando Spring Boot 3, Spring AI, Tool Calling, transcrição de voz (Whisper) e síntese de áudio (TTS). " +
                                "Desenvolvido como projeto final do módulo de IA na DIO.")
                        .contact(new Contact()
                                .name("Victor Antonio Campos")
                                .url("https://github.com/Vitao91")
                                .email("victor78antonio.com@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
