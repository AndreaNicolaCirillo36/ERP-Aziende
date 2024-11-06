package com.azienda.erp.erp_backend.auth;

import com.azienda.erp.erp_backend.exception.ErrorResponse;
import com.azienda.erp.erp_backend.exception.InvalidRefreshTokenException;
import com.azienda.erp.erp_backend.security.JwtUtil;
import com.azienda.erp.erp_backend.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servizio per la gestione delle operazioni di autenticazione e dei token.
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Autentica un utente con username e password e genera i token JWT.
     * @param request - Richiesta di autenticazione contenente username e password.
     * @return ResponseEntity con i token di autenticazione o un errore.
     */
    public ResponseEntity<Object> authenticateUser(AuthenticationRequest request) {
        try {
            logger.info("Tentativo di autenticazione per l'utente: {}", request.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            logger.info("Autenticazione riuscita per l'utente: {}", request.getUsername());
            return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            logger.warn("Autenticazione fallita per l'utente: {} - Errore: {}", request.getUsername(), e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Credenziali non valide", HttpStatus.UNAUTHORIZED.value(), Collections.emptyList());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            logger.error("Errore imprevisto durante il login per l'utente: {}", request.getUsername(), e);
            ErrorResponse errorResponse = new ErrorResponse("Errore del server", HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Rinnova il token di accesso utilizzando il refresh token.
     * @param refreshTokenRequest - Richiesta contenente il refresh token.
     * @return ResponseEntity con i nuovi token di autenticazione o un errore.
     */
    public ResponseEntity<Object> refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            logger.warn("Refresh token non valido");
            ErrorResponse errorResponse = new ErrorResponse("Il Refresh Token è scaduto o non valido.", HttpStatus.UNAUTHORIZED.value(), Collections.emptyList());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            if (Boolean.TRUE.equals(jwtUtil.validateRefreshToken(refreshToken))) {
                String username = jwtUtil.extractUsername(refreshToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String newAccessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
                return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, newRefreshToken));
            } else {
                throw new InvalidRefreshTokenException("Refresh token non valido o scaduto.");
            }
        } catch (InvalidRefreshTokenException e) {
            logger.warn("Tentativo di rinnovo fallito: Refresh Token non valido o scaduto - Errore: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Il Refresh Token è scaduto o non valido.", HttpStatus.UNAUTHORIZED.value(), Collections.emptyList());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            logger.error("Errore imprevisto durante il rinnovo del token: {}", e.getMessage(), e);
            ErrorResponse errorResponse = new ErrorResponse("Errore del server", HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
