package com.azienda.erp.erp_backend.security;

import com.azienda.erp.erp_backend.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    // Ruolo Admin definito come costante per evitare ripetizioni
    private static final String ADMIN_ROLE = "ADMIN";

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configura il filtro di sicurezza principale della Spring Security.
     *
     * @param http Oggetto per la configurazione delle richieste HTTP.
     * @return SecurityFilterChain configurato.
     * @throws Exception Lancia eccezioni nel caso ci siano errori di configurazione.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and() // Abilita CORS (configurabile ulteriormente da WebConfig)
                .csrf().disable() // Disabilita CSRF per le API stateless
                .authorizeHttpRequests()

                // Percorsi pubblici accessibili a chiunque
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // Percorsi per la registrazione di nuovi utenti (accessibili solo agli amministratori)
                .requestMatchers("/api/users/register", "/api/users/defaultUser", "/api/products/**", "/api/suppliers/**").hasRole(ADMIN_ROLE)

                // Metodi DELETE sono accessibili solo agli amministratori
                .requestMatchers(HttpMethod.DELETE, "/api/users/**", "/api/sales/**", "/api/products/**", "/api/suppliers/**").hasRole(ADMIN_ROLE)

                // Tutti gli altri percorsi richiedono autenticazione
                .anyRequest().authenticated()
                .and()

                // Imposta la sessione come stateless (visto che si usano JWT)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Aggiungi il filtro JWT prima del filtro di autenticazione standard
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura l'oggetto AuthenticationManager con il servizio di dettagli utente e un encoder password.
     *
     * @param http Oggetto per la configurazione delle richieste HTTP.
     * @param passwordEncoder PasswordEncoder per cifrare la password.
     * @return Un'istanza di AuthenticationManager.
     * @throws Exception Lancia eccezioni se ci sono errori di configurazione.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    /**
     * Definisce l'algoritmo di cifratura per le password degli utenti (BCrypt).
     *
     * @return Un'istanza di PasswordEncoder basata su BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
