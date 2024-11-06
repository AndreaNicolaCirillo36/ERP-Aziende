package com.azienda.erp.erp_backend.exception;

public class DuplicateBarcodeException extends RuntimeException {

    public DuplicateBarcodeException(String message) {
        super(message);
    }
}
