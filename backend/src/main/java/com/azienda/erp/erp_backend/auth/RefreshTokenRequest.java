package com.azienda.erp.erp_backend.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    @NotNull(message = "Il refresh token non può essere nullo.")
    private String refreshToken;
}