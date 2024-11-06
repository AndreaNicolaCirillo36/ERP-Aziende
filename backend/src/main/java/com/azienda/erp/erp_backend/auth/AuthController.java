package com.azienda.erp.erp_backend.auth;

import com.azienda.erp.erp_backend.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller per la gestione delle operazioni di autenticazione.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Costruttore per AuthController.
     *
     * @param authService Servizio di autenticazione iniettato per gestire le operazioni di login e token.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint per effettuare il login.
     *
     * @param request Contiene le credenziali dell'utente.
     * @return ResponseEntity con i token di autenticazione o un errore.
     */
    @Operation(summary = "Effettua il login", description = "Endpoint per effettuare il login e ottenere un token di accesso.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Contiene le credenziali dell'utente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationRequest.class),
                            examples = @ExampleObject(value = "{\"username\": \"utente1\", \"password\": \"Password123!\"}")))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticazione avvenuta con successo",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class),
                            examples = @ExampleObject(value = "{\"accessToken\": \"jwt-token\", \"refreshToken\": \"refresh-jwt-token\"}"))),
            @ApiResponse(responseCode = "401", description = "Credenziali non valide",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> login(@Valid @RequestBody AuthenticationRequest request) {
        return authService.authenticateUser(request);
    }

    /**
     * Endpoint per rinnovare il token di accesso.
     *
     * @param refreshTokenRequest Contiene il refresh token.
     * @return ResponseEntity con i nuovi token o un errore.
     */
    @Operation(summary = "Rinnova il token di accesso", description = "Endpoint per rinnovare il token di accesso utilizzando il refresh token.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(value = "{\"refreshToken\": \"refresh-jwt-token\"}")))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token rinnovato con successo",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class),
                            examples = @ExampleObject(value = "{\"accessToken\": \"new-jwt-token\", \"refreshToken\": \"new-refresh-jwt-token\"}"))),
            @ApiResponse(responseCode = "401", description = "Refresh token non valido o scaduto",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/refresh-token", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshAccessToken(refreshTokenRequest);
    }
}