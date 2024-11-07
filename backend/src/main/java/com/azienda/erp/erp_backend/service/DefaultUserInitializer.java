package com.azienda.erp.erp_backend.service;

import com.azienda.erp.erp_backend.entity.User;
import com.azienda.erp.erp_backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Classe che si occupa dell'inizializzazione dell'utente di default "admin" al primo avvio dell'applicazione.
 * Se non ci sono utenti presenti nel database, viene creato un utente amministratore con credenziali predefinite.
 */
@Component
public class DefaultUserInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Costruttore della classe DefaultUserInitializer.
     *
     * @param userRepository Repository degli utenti per accedere al database degli utenti.
     * @param passwordEncoder Componente di codifica delle password per garantire la sicurezza.
     */
    public DefaultUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Metodo che crea l'utente di default "admin" con il ruolo di amministratore.
     * Questo metodo viene chiamato automaticamente al termine della costruzione del bean (annotazione @PostConstruct).
     * Se non ci sono utenti presenti nel database, viene creato un utente con username "admin" e password "admin123".
     */
    @PostConstruct
    public void createDefaultUser() {
        if (userRepository.count() == 0) {
            User defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword(passwordEncoder.encode("Admin123!")); // Codifica la password per sicurezza
            defaultUser.setRole("ADMIN");
            userRepository.save(defaultUser); // Salva l'utente nel database
            logger.info("Utente di default creato con successo: admin/admin123");
        }
    }
}
