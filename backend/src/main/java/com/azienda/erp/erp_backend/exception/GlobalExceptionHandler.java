package com.azienda.erp.erp_backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.ObjectError;

/**
 * Gestore globale delle eccezioni per l'applicazione.
 * Fornisce la gestione centralizzata delle eccezioni per i controller.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Costanti per messaggi di errore
    private static final String GENERIC_ERROR_MESSAGE = "Si è verificato un errore. Riprova più tardi.";
    private static final String BAD_CREDENTIALS_MESSAGE = "Credenziali non valide. Controlla il nome utente e la password.";
    private static final String USER_NOT_FOUND_MESSAGE = "Nome utente non trovato.";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Prodotto non trovato.";
    private static final String SUPPLIER_NOT_FOUND_MESSAGE = "Fornitore non trovato.";
    private static final String SALE_NOT_FOUND_MESSAGE = "Vendita non trovata.";
    private static final String INSUFFICIENT_PRODUCT_QUANTITY_MESSAGE = "Quantità di prodotto insufficiente.";
    private static final String USERNAME_ALREADY_EXISTS_MESSAGE = "Nome utente già esistente.";
    private static final String ACCESS_DENIED_MESSAGE = "Accesso negato. Non hai i permessi per eseguire questa operazione.";
    private static final String VALIDATION_ERROR_MESSAGE = "Errore di validazione.";
    private static final String MESSAGE_NOT_READABLE_ERROR = "Il messaggio della richiesta non è leggibile o è malformato.";
    private static final String DUPLICATE_BARCODE_MESSAGE = "Il barcode deve essere unico!";

    /**
     * Gestisce l'eccezione BadCredentialsException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 401 (UNAUTHORIZED).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("Credenziali non valide: ", ex);
        ErrorResponse error = new ErrorResponse(BAD_CREDENTIALS_MESSAGE, HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Gestisce l'eccezione RuntimeException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 500 (INTERNAL_SERVER_ERROR).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Errore durante l'esecuzione: ", ex);
        ErrorResponse error = new ErrorResponse(GENERIC_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Gestisce eccezioni generiche non previste.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 500 (INTERNAL_SERVER_ERROR).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Errore inatteso: ", ex);
        ErrorResponse error = new ErrorResponse(GENERIC_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Gestisce l'eccezione UsernameNotFoundException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 404 (NOT_FOUND).
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.warn("Nome utente non trovato: ", ex);
        ErrorResponse error = new ErrorResponse(USER_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Gestisce l'eccezione UsernameAlreadyExistsException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 400 (BAD_REQUEST).
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        logger.warn("Nome utente già esistente: ", ex);
        ErrorResponse error = new ErrorResponse(USERNAME_ALREADY_EXISTS_MESSAGE, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gestisce l'eccezione ProductNotFoundException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 404 (NOT_FOUND).
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        logger.warn("Prodotto non trovato: ", ex);
        ErrorResponse error = new ErrorResponse(PRODUCT_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Gestisce l'eccezione SupplierNotFoundException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 404 (NOT_FOUND).
     */
    @ExceptionHandler(SupplierNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSupplierNotFoundException(SupplierNotFoundException ex) {
        logger.warn("Fornitore non trovato: ", ex);
        ErrorResponse error = new ErrorResponse(SUPPLIER_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Gestisce l'eccezione SaleNotFoundException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 404 (NOT_FOUND).
     */
    @ExceptionHandler(SaleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSaleNotFoundException(SaleNotFoundException ex) {
        logger.warn("Vendita non trovata: ", ex);
        ErrorResponse error = new ErrorResponse(SALE_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Gestisce l'eccezione DataIntegrityViolationException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 409 (CONFLICT).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.warn("Conflitto nei dati: ", ex);
        ErrorResponse error = new ErrorResponse(DUPLICATE_BARCODE_MESSAGE, HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Gestisce l'eccezione InsufficientProductQuantityException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 400 (BAD_REQUEST).
     */
    @ExceptionHandler(InsufficientProductQuantityException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientProductQuantityException(InsufficientProductQuantityException ex) {
        logger.warn("Quantità di prodotto insufficiente: ", ex);
        ErrorResponse error = new ErrorResponse(INSUFFICIENT_PRODUCT_QUANTITY_MESSAGE, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gestisce l'eccezione InvalidRefreshTokenException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 403 (FORBIDDEN).
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        logger.error("Refresh token non valido o scaduto: ", ex);
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Gestisce l'eccezione InvalidCredentialsException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 401 (UNAUTHORIZED).
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        logger.warn("Credenziali non valide fornite: ", ex);
        ErrorResponse error = new ErrorResponse(BAD_CREDENTIALS_MESSAGE, HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Gestisce l'eccezione TokenExpiredException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 401 (UNAUTHORIZED).
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
        logger.warn("Token scaduto: ", ex);
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Gestisce l'eccezione InvalidTokenException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 400 (BAD_REQUEST).
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        logger.error("Token non valido: ", ex);
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    /**
     * Gestisce l'eccezione ResourceNotFoundException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 404 (NOT_FOUND).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Risorsa non trovata: ", ex);
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Gestisce l'eccezione AccessDeniedException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 403 (FORBIDDEN).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Accesso negato: ", ex);
        ErrorResponse error = new ErrorResponse(ACCESS_DENIED_MESSAGE, HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Gestisce l'eccezione MethodArgumentNotValidException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 400 (BAD_REQUEST).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        logger.warn("Errore di validazione: ", ex);
        ErrorResponse error = new ErrorResponse(VALIDATION_ERROR_MESSAGE, HttpStatus.BAD_REQUEST.value(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gestisce l'eccezione HttpMessageNotReadableException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 400 (BAD_REQUEST).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("Messaggio della richiesta non leggibile: ", ex);
        ErrorResponse error = new ErrorResponse(MESSAGE_NOT_READABLE_ERROR, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gestisce l'eccezione DuplicateBarcodeException.
     *
     * @param ex Eccezione sollevata.
     * @return Risposta HTTP con codice 400 (BAD_REQUEST).
     */
    @ExceptionHandler(DuplicateBarcodeException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateBarcodeException(DuplicateBarcodeException ex) {
        logger.warn("Barcode duplicato: ", ex);
        ErrorResponse error = new ErrorResponse(DUPLICATE_BARCODE_MESSAGE, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}