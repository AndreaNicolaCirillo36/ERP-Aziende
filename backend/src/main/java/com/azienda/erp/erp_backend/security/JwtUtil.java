package com.azienda.erp.erp_backend.security;

import com.azienda.erp.erp_backend.exception.InvalidTokenException;
import com.azienda.erp.erp_backend.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility per la gestione dei JWT, generazione e validazione.
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expirationAccessTokenMs}")
    private int jwtExpirationAccessTokenMs;

    @Value("${jwt.expirationRefreshTokenMs}")
    private int jwtExpirationRefreshTokenMs;

    /**
     * Estrae l'username dal token.
     * @param token - Token JWT.
     * @return - Username presente nel token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Estrae un particolare claim dal token JWT.
     * @param token - Token JWT.
     * @param claimsResolver - Funzione per risolvere il claim.
     * @param <T> - Tipo del claim.
     * @return - Il valore del claim estratto.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Estrae tutti i claim dal token.
     * @param token - Token JWT.
     * @return - Claims del token.
     */
    private Claims extractAllClaims(String token) {
        try {
            logger.info("Parsing JWT token...");
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT scaduto: " + e.getMessage());
            throw new TokenExpiredException("Token JWT scaduto", e);
        } catch (Exception e) {
            logger.error("Errore durante il parsing del token JWT: " + e.getMessage());
            throw new InvalidTokenException("Token JWT non valido o malformato", e);
        }
    }

    /**
     * Estrae il ruolo presente nel token JWT.
     * @param token - Token JWT.
     * @return - Lista dei ruoli.
     */
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        String role = (String) claims.get("role");
        return List.of(role);
    }

    /**
     * Metodo per generare il token JWT.
     * @param claims - I claims da includere nel token.
     * @param subject - Soggetto del token (di solito username).
     * @param expirationTime - Tempo di scadenza del token.
     * @return - Token JWT generato.
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    /**
     * Genera un token di accesso per l'utente.
     * @param userDetails - Dettagli dell'utente.
     * @return - Token di accesso generato.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", ACCESS_TOKEN_TYPE);
        claims.put("role", userDetails.getAuthorities().stream()
                .findFirst()
                .get()
                .getAuthority());
        return createToken(claims, userDetails.getUsername(), jwtExpirationAccessTokenMs);
    }

    /**
     * Genera un token di refresh per l'utente.
     * @param userDetails - Dettagli dell'utente.
     * @return - Token di refresh generato.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", REFRESH_TOKEN_TYPE);
        return createToken(claims, userDetails.getUsername(), jwtExpirationRefreshTokenMs); // 10 ore
    }

    /**
     * Valida un token di accesso.
     * @param token - Token da validare.
     * @param userDetails - Dettagli dell'utente associato.
     * @return - true se il token è valido, altrimenti false.
     */
    public Boolean validateAccessToken(String token, UserDetails userDetails) {
        return validateToken(token, userDetails, ACCESS_TOKEN_TYPE);
    }

    /**
     * Valida un refresh token.
     * @param token - Token di refresh da validare.
     * @return - true se il token è valido, altrimenti false.
     */
    public Boolean validateRefreshToken(String token) {
        return validateToken(token, null, REFRESH_TOKEN_TYPE);
    }

    /**
     * Metodo generico per validare il token.
     * @param token - Token da validare.
     * @param userDetails - Dettagli dell'utente associato (null se non richiesto).
     * @param expectedTokenType - Tipo di token atteso ("ACCESS" o "REFRESH").
     * @return - true se il token è valido, altrimenti false.
     */
    private Boolean validateToken(String token, UserDetails userDetails, String expectedTokenType) {
        try {
            Claims claims = extractAllClaims(token);
            if (!expectedTokenType.equals(claims.get("type"))) {
                logger.error("Il token fornito non è un " + expectedTokenType.toLowerCase() + " token.");
                return false;
            }
            if (userDetails != null) {
                final String username = claims.getSubject();
                return username.equals(userDetails.getUsername()) && !claims.getExpiration().before(new Date());
            } else {
                return !claims.getExpiration().before(new Date());
            }
        } catch (TokenExpiredException e) {
            logger.warn(expectedTokenType + " token scaduto: " + e.getMessage());
            return false;
        } catch (InvalidTokenException e) {
            logger.error(expectedTokenType + " token non valido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Estrae le autorità dal token JWT.
     * @param token - Token JWT.
     * @return - Lista di SimpleGrantedAuthority.
     */
    public List<SimpleGrantedAuthority> getUserAuthoritiesFromToken(String token) {
        List<String> roles = extractRoles(token);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
