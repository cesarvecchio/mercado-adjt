package br.com.mspedidos.application.controller.exceptions;

public class PagamentoException extends RuntimeException {
    public PagamentoException(String message) {
        super(message);
    }

    public PagamentoException(String message, Throwable cause) {
        super(message, cause);
    }
}
