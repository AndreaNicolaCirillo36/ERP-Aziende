package com.azienda.erp.erp_backend.controller;

import com.azienda.erp.erp_backend.entity.User;
import com.azienda.erp.erp_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione degli utenti nell'applicazione ERP.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

/**
 * Endpoint per verificare se l'utente di default 'admin' è presente nel sistema.
 *
 * @return true se l'utente di default 'admin' è presente, altrimenti false.
 */
    @Operation(summary = "Verifica la presenza dell'utente di default", description = "Endpoint per verificare se l'utente di default 'admin' è presente nel sistema.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Presenza dell'utente di default verificata con successo",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    @GetMapping("/defaultUser")
    public boolean isDefaultUserPresent() {
        return userService.isDefaultUserPresent();
    }

    /**
     * Endpoint per registrare un nuovo utente.
     *
     * @param user l'utente da registrare
     * @return un messaggio di successo se l'utente viene registrato con successo, altrimenti un errore 400 con un messaggio di errore
     */
    @Operation(summary = "Registra un nuovo utente", description = "Endpoint per registrare un nuovo utente.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utente registrato con successo",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Errore: Username già esistente",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid User user) {
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Utente registrato con successo");
    }

    /**
     * Endpoint per eliminare un utente a partire dal suo ID.
     *
     * @param id l'ID dell'utente da eliminare
     * @return un messaggio di successo se l'utente viene eliminato con successo, altrimenti un errore 404 con un messaggio di errore
     */
    @Operation(summary = "Elimina un utente", description = "Endpoint per eliminare un utente a partire dal suo ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utente eliminato con successo",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Utente non trovato",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
