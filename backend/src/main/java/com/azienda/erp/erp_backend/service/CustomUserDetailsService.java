package com.azienda.erp.erp_backend.service;

import com.azienda.erp.erp_backend.entity.User;
import com.azienda.erp.erp_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementazione del servizio UserDetailsService di Spring Security
 * per il caricamento dei dettagli dell'utente dall'archivio dati.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carica un utente dall'archivio dati in base al suo username.
     * Questo metodo viene utilizzato da Spring Security durante il processo di autenticazione.
     *
     * @param username Il nome utente dell'utente da caricare.
     * @return Un oggetto UserDetails contenente i dettagli dell'utente, incluso il nome utente, la password e il ruolo.
     * @throws UsernameNotFoundException Se l'utente con il nome specificato non viene trovato.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Costruisce l'oggetto UserDetails da passare a Spring Security per la gestione dell'autenticazione.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
