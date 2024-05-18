package br.com.mspedidos.application.controller.exceptions;

public class EstoqueException extends RuntimeException {
    public EstoqueException(String message) {
        super(message);
    }

    public EstoqueException(String message, Throwable cause) {
        super(message, cause);
    }
}
