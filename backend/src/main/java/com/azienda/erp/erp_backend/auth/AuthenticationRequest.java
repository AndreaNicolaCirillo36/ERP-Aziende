package com.azienda.erp.erp_backend.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {

    @NotNull(message = "Il nome utente non può essere nullo.")
    @Size(min = 3, max = 50, message = "Il nome utente deve avere tra 3 e 50 caratteri.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Il nome utente può contenere solo caratteri alfanumerici.")
    private String username;

    @NotNull(message = "La password non può essere nulla.")
    @Size(min = 8, max = 100, message = "La password deve avere almeno 8 caratteri.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).+$",
            message = "La password deve contenere almeno una lettera maiuscola, una minuscola, un numero e un carattere speciale.")
    private String password;
}
