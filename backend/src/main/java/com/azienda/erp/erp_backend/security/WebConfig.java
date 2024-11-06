package com.azienda.erp.erp_backend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurazione CORS per consentire richieste cross-origin al backend.
 * Questa configurazione permette al frontend (ospitato su indirizzi localhost durante lo sviluppo)
 * di interagire con il backend senza restrizioni di origine.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Metodo per configurare le politiche CORS per l'intero backend.
     *
     * @param registry L'oggetto CorsRegistry usato per definire le configurazioni CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Consente CORS su tutte le rotte del server.
                .allowedOrigins("http://localhost:5173", "http://localhost:5174") // Origini consentite (frontend in sviluppo).
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Metodi HTTP consentiti.
                .allowedHeaders("Authorization", "Content-Type", "Accept", "X-Requested-With") // Headers consentiti nelle richieste.
                .allowCredentials(true); // Permette l'invio di credenziali (cookie, headers di autenticazione, ecc.).
    }
}
