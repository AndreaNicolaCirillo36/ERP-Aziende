package com.azienda.erp.erp_backend.security;

import com.azienda.erp.erp_backend.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro personalizzato per gestire le richieste JWT.
 * Estende {@link OncePerRequestFilter} per garantire che ogni richiesta venga filtrata una volta.
 * Questo filtro intercetta tutte le richieste HTTP per estrarre e validare il token JWT,
 * verificando se l'utente è autenticato e impostando il contesto di sicurezza.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Metodo principale che filtra ogni richiesta HTTP per verificare la presenza di un token JWT valido.
     * Se il token è valido, imposta l'autenticazione nel contesto di sicurezza di Spring.
     *
     * @param request La richiesta HTTP ricevuta.
     * @param response La risposta HTTP da inviare.
     * @param chain La catena di filtri a cui passare la richiesta e la risposta.
     * @throws ServletException In caso di errore del servlet.
     * @throws IOException In caso di errore di I/O.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Ottiene l'intestazione Authorization dalla richiesta.
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        try {
            // Verifica se l'intestazione contiene un token Bearer.
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);  // Rimuovi "Bearer " dall'inizio del token
                logger.info("Token ricevuto: " + jwt);
                username = jwtUtil.extractUsername(jwt);  // Estrai il nome utente dal token JWT
            }

            // Se il nome utente è valido e non c'è già un'autenticazione nel contesto, continua con la validazione.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Verifica se il token è valido
                if (Boolean.TRUE.equals(jwtUtil.validateAccessToken(jwt, userDetails))) {
                    // Utilizza il nuovo metodo per ottenere le autorità dal token
                    List<SimpleGrantedAuthority> authorities = jwtUtil.getUserAuthoritiesFromToken(jwt);

                    // Assegna l'autenticazione con le autorità estratte
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Imposta il contesto di sicurezza con l'autenticazione corrente
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT scaduto", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Il token JWT è scaduto");
            return;
        } catch (Exception e) {
            logger.error("Errore nella validazione del token JWT", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT non valido");
            return;
        }

        // Passa la richiesta e la risposta alla catena successiva di filtri.
        chain.doFilter(request, response);
    }
}
