package br.com.andreluismain.editaisic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de documentação interativa da API via OpenAPI 3.0 / Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agente Analisador de Editais de IC")
                        .version("1.0.0")
                        .description("API REST para coleta, extração e análise de aderência de editais acadêmicos com Google Gemini Pro.")
                        .contact(new Contact()
                                .name("André Luís")
                                .url("https://github.com/AndreLuismain"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
