package com.azienda.erp.erp_backend.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configurazione per Swagger/OpenAPI per il sistema ERP.
 * Questa configurazione definisce le informazioni principali dell'API, inclusi i server di sviluppo e produzione,
 * la sicurezza e le informazioni di contatto.
 */
@Configuration
public class SwaggerConfiguration {

    @Value("${andreaNicolaCirillo.openapi.dev-url}")
    private String devUrl;

    @Value("${andreaNicolaCirillo.openapi.prod-url}")
    private String prodUrl;

    /**
     * Configura un'istanza di OpenAPI con le informazioni dell'API, i server, la sicurezza e altri dettagli.
     *
     * @return un oggetto OpenAPI con le configurazioni specificate.
     */
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL per l'ambiente di sviluppo");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL per l'ambiente di produzione");

        Contact contact = new Contact();
        contact.setEmail("nicolacirillo3621@gmail.com");
        contact.setName("Andrea Nicola Cirillo");
        contact.setUrl("https://www.linkedin.com/in/andrea-nicola-cirillo");

        License mitLicense = new License().name("Licenza MIT").url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("ERP Management API")
                .version("1.0")
                .contact(contact)
                .description("Questa API espone endpoint per la gestione del sistema ERP, inclusi moduli di gestione utenti, ordini, prodotti e altre funzionalità tipiche di un ERP.")
                .termsOfService("https://example.com/terms")
                .license(mitLicense);

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Bearer Authentication");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Authentication");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("Bearer Authentication", securityScheme))
                .servers(List.of(devServer, prodServer));
    }
}
